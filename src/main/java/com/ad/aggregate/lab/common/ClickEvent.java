package com.ad.aggregate.lab.common;

public record ClickEvent(
        AdEventBase base
) implements AdEvent {
}
