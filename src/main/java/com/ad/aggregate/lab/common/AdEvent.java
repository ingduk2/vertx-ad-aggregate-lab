package com.ad.aggregate.lab.common;

public sealed interface AdEvent permits RequestEvent, ImpressionEvent, ClickEvent {
    AdEventBase base();
}
