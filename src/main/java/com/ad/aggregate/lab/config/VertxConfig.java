package com.ad.aggregate.lab.config;

import com.ad.aggregate.lab.aggregate.AggregateBulkService;
import com.ad.aggregate.lab.verticle.model.FlushPayload;
import com.ad.aggregate.lab.verticle.model.FlushPayloadCodec;
import com.ad.aggregate.lab.verticle.AggregationVerticle;
import com.ad.aggregate.lab.verticle.BulkUpsertWorkerVerticle;
import com.ad.aggregate.lab.verticle.KafkaConsumerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Configuration
public class VertxConfig {

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @Bean
    public KafkaConsumerVerticle kafkaConsumerVerticle(
            Map<String, String> kafkaConsumerConfig
    ) {
        return new KafkaConsumerVerticle(kafkaConsumerConfig);
    }

    @Bean
    public AggregationVerticle aggregationVerticle(
            ObjectMapper objectMapper,
            AggregateProperties aggregateProperties
    ) {
        return new AggregationVerticle(objectMapper, aggregateProperties);
    }

    @Bean
    public BulkUpsertWorkerVerticle bulkUpsertWorkerVerticle(AggregateBulkService aggregateBulkService) {
        return new BulkUpsertWorkerVerticle(aggregateBulkService);
    }

    @Bean
    public ApplicationRunner deployVerticles(
            Vertx vertx,
            KafkaConsumerVerticle kafkaConsumerVerticle,
            AggregationVerticle aggregationVerticle,
            BulkUpsertWorkerVerticle bulkUpsertWorkerVerticle
    ) {
        return args -> {
            vertx.eventBus().registerDefaultCodec(FlushPayload.class, new FlushPayloadCodec());

            vertx.deployVerticle(kafkaConsumerVerticle)
                    .onSuccess(id -> log.info("KafkaConsumerVerticle deployed. ID: {}", id))
                    .onFailure(err -> log.error("KafkaConsumerVerticle deployment failed.", err));

            vertx.deployVerticle(aggregationVerticle)
                    .onSuccess(id -> log.info("AggregationVerticle deployed. ID: {}", id))
                    .onFailure(err -> log.error("AggregationVerticle deployment failed.", err));

            vertx.deployVerticle(bulkUpsertWorkerVerticle,
                            new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER))
                    .onSuccess(id -> log.info("BulkUpsertWorkerVerticle deployed. ID: {}", id))
                    .onFailure(err -> log.error("BulkUpsertWorkerVerticle deployment failed.", err));
        };
    }
}
