package com.ad.aggregate.lab.common;

import com.ad.aggregate.lab.verticle.AggregateCounter;

import java.util.Map;

public record FlushPayload(
        Map<String, AggregateCounter> buffer
) {
}
