package com.ad.aggregate.lab.verticle;

import com.ad.aggregate.lab.common.*;
import com.ad.aggregate.lab.config.AggregateProperties;
import com.ad.aggregate.lab.event.*;
import com.ad.aggregate.lab.verticle.model.AggregateCounter;
import com.ad.aggregate.lab.verticle.model.FlushPayload;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ObjLongConsumer;

@Slf4j
@RequiredArgsConstructor
public class AggregationVerticle extends AbstractVerticle {

    private final ObjectMapper objectMapper;
    private final AggregateProperties aggregateProperties;
    private final Map<String, AggregateCounter> buffer = new HashMap<>();

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.eventBus().<String>consumer(
                AdTopic.AD_REQUEST.getValue(),
                message -> handle(message.body(), RequestEvent.class, AggregateCounter::addRequest)
        );

        vertx.eventBus().<String>consumer(
                AdTopic.AD_IMPRESSION.getValue(),
                message -> handle(message.body(), ImpressionEvent.class, AggregateCounter::addImpression)
        );

        vertx.eventBus().<String>consumer(
                AdTopic.AD_CLICK.getValue(),
                message -> handle(message.body(), ClickEvent.class, AggregateCounter::addClick)
        );

        vertx.setPeriodic(
                aggregateProperties.flushIntervalSeconds() * 1000L,
                id -> flush(FlushReason.PERIODIC)
        );

        startPromise.complete();
    }

    private <T extends AdEvent> void handle(String body, Class<T> clazz, ObjLongConsumer<AggregateCounter> action) {
        log.info("body: {}", body);
        T event = objectMapper.readValue(body, clazz);
        AdEventBase base = event.base();
        String key = base.buildKey();

        AggregateCounter aggregateCounter = buffer.computeIfAbsent(
                key,
                k -> AggregateCounter.create(base.placementId(), base.date(), base.hour())
        );
        long count = base.count();
        action.accept(aggregateCounter, count);
        log.info("aggregate - key: {}, buffer: {}", key, buffer.get(key));

        // 건수 초과 flush
        if (buffer.size() >= aggregateProperties.flushBufferSize()) {
            flush(FlushReason.BUFFER_SIZE);
        }
    }

    private void flush(FlushReason reason) {
        if (buffer.isEmpty()) return;
        log.info("flush Start - reason: {}, buffer size: {}", reason, buffer.size());

        Map<String, AggregateCounter> snapshot = new HashMap<>(buffer);
        buffer.clear();

        vertx.eventBus().request(EventBusAddress.AGGREGATE_FLUSH.address(), new FlushPayload(snapshot))
                .onSuccess(reply -> log.info("flush response : {}", reply.body()))
                .onFailure(error -> {
                    log.error("flush fail : {}", error.getMessage());
                    snapshot.forEach((key, counter) ->
                            buffer.merge(key, counter, (existing, failed) -> {
                                existing.addRequest(failed.getRequest());
                                existing.addImpression(failed.getImpression());
                                existing.addClick(failed.getClick());
                                return existing;
                            })
                    );
                });
    }
}
