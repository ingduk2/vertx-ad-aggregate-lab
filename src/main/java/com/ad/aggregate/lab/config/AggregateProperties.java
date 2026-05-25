package com.ad.aggregate.lab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aggregate")
public record AggregateProperties(
        int flushIntervalSeconds,
        int flushBufferSize
) {
}
