package com.ad.aggregate.lab.common;

public record AdEventBase(
        Long placementId,
        String date,
        String hour,
        long count
) {
}
