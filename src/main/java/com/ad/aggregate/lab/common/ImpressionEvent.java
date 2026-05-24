package com.ad.aggregate.lab.common;

public record ImpressionEvent(
        AdEventBase base
) implements AdEvent {
}
