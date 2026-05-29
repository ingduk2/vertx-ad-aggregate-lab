package com.ad.aggregate.lab.event;

public record ImpressionEvent(
        AdEventBase base
) implements AdEvent {
}
