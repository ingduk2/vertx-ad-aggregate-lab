package com.ad.aggregate.lab.config;

import com.ad.aggregate.lab.verticle.KafkaConsumerVerticle;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public ApplicationRunner deployVerticles(
            Vertx vertx,
            KafkaConsumerVerticle kafkaConsumerVerticle
    ) {
        return args -> {
            vertx.deployVerticle(kafkaConsumerVerticle)
                    .onSuccess(id -> log.info("KafkaConsumerVerticle deployed. ID: {}", id))
                    .onFailure(err -> log.error("KafkaConsumerVerticle deployment failed.", err));
        };
    }
}
