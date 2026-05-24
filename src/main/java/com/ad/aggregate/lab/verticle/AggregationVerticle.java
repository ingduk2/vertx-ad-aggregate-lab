package com.ad.aggregate.lab.verticle;

import com.ad.aggregate.lab.common.*;
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

        startPromise.complete();
    }

    private <T extends AdEvent> void handle(String body, Class<T> clazz, ObjLongConsumer<AggregateCounter> action) {
        log.info("body: {}", body);
        T event = objectMapper.readValue(body, clazz);
        String key = buildKey(event.base());

        AggregateCounter aggregateCounter = buffer.computeIfAbsent(key, k -> new AggregateCounter());
        long count = event.base().count();
        action.accept(aggregateCounter, count);
        log.info("aggregate - key: {}, buffer: {}", key, buffer.get(key));
    }

    private String buildKey(AdEventBase base) {
        return base.placementId() + "_" + base.date() + "_" + base.hour();
    }
}
