package com.ad.aggregate.lab.event;

public record RequestEvent(
        AdEventBase base
) implements AdEvent {
}
