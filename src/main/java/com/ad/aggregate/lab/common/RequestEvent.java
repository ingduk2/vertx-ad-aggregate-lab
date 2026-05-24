package com.ad.aggregate.lab.common;

public record RequestEvent(
        AdEventBase base
) implements AdEvent {
}
