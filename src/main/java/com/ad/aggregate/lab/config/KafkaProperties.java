package com.ad.aggregate.lab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
        String bootstrapServers,
        String groupId,
        String keyDeserializer,
        String valueDeserializer,
        String autoOffsetReset,
        List<String> topics
) {
}
