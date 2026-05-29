package com.ad.aggregate.lab.verticle.model;

import java.util.Map;

public record FlushPayload(
        Map<String, AggregateCounter> buffer
) {
}
