package com.ad.aggregate.lab.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerVerticle extends AbstractVerticle {

    private final Map<String, String> kafkaConfig;
    private final List<String> topics;
    private KafkaConsumer<String, String> consumer;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        consumer = KafkaConsumer.create(vertx, kafkaConfig);

        consumer.subscribe(new HashSet<>(topics))
                .onSuccess(v -> {
                    log.info("Kafka Consumer Started");
                    startPromise.complete();
                })
                .onFailure(startPromise::fail);

        consumer.handler(record -> {
            log.info("topic: {}, key: {}, value: {}", record.topic(), record.key(), record.value());
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        consumer.close()
                .onSuccess(v -> stopPromise.complete())
                .onFailure(stopPromise::fail);
    }
}
