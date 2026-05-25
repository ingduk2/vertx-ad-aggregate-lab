package com.ad.aggregate.lab.verticle;

import com.ad.aggregate.lab.aggregate.AggregateService;
import com.ad.aggregate.lab.common.EventBusAddress;
import com.ad.aggregate.lab.common.FlushPayload;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BulkUpsertWorkerVerticle extends AbstractVerticle {

    private final AggregateService aggregateService;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().<FlushPayload>consumer(EventBusAddress.AGGREGATE_FLUSH.address(), message -> {
            FlushPayload payload = message.body();
            log.info("flush - buffer size: {}, body: {}", payload.buffer().size(), payload);
            // TODO: DB Upsert
        });

        startPromise.complete();
    }
}
