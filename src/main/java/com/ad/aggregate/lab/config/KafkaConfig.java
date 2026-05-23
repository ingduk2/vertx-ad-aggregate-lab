package com.ad.aggregate.lab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public Map<String, String> kafkaConsumerConfig(KafkaProperties kafkaProperties) {
        return Map.of(
                "bootstrap.servers", kafkaProperties.bootstrapServers(),
                "key.deserializer", kafkaProperties.keyDeserializer(),
                "value.deserializer", kafkaProperties.valueDeserializer(),
                "group.id", kafkaProperties.groupId(),
                "auto.offset.reset", kafkaProperties.autoOffsetReset()
        );
    }
}
