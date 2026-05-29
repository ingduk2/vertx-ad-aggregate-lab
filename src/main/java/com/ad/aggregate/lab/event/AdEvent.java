package com.ad.aggregate.lab.event;

public sealed interface AdEvent permits RequestEvent, ImpressionEvent, ClickEvent {
    AdEventBase base();
}
