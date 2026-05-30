package com.ad.aggregate.lab.aggregate;

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
}
