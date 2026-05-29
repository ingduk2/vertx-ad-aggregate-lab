package com.ad.aggregate.lab.event;

public record ClickEvent(
        AdEventBase base
) implements AdEvent {
}
