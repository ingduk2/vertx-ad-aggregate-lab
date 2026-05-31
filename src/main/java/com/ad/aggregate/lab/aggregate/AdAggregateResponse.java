package com.ad.aggregate.lab.aggregate;

import com.ad.aggregate.lab.verticle.model.AggregateCounter;

public record AdAggregateResponse(
        Long placementId,
        String date,
        String hour,
        long requestCount,
        long impressionCount,
        long clickCount
) {
    public static AdAggregateResponse from(AdAggregate adAggregate) {
        return new AdAggregateResponse(
                adAggregate.getPlacementId(),
                adAggregate.getDate(),
                adAggregate.getHour(),
                adAggregate.getRequestCount(),
                adAggregate.getImpressionCount(),
                adAggregate.getClickCount()
        );
    }

    public static AdAggregateResponse fromCounter(AggregateCounter aggregateCounter) {
        return new AdAggregateResponse(
                aggregateCounter.getPlacementId(),
                aggregateCounter.getDate(),
                aggregateCounter.getHour(),
                aggregateCounter.getRequest(),
                aggregateCounter.getImpression(),
                aggregateCounter.getClick()
        );
    }
}
