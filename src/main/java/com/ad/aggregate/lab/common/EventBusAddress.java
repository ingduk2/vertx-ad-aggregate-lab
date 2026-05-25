package com.ad.aggregate.lab.common;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventBusAddress {
    AGGREGATE_FLUSH("aggregate.flush");

    private final String address;

    public String address() {
        return address;
    }
}
