package com.ad.aggregate.lab.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum AdTopic {
    AD_REQUEST("ad.request"),
    AD_IMPRESSION("ad.impression"),
    AD_CLICK("ad.click");

    private final String value;

    public static Set<String> toSet() {
        return Arrays.stream(values())
                .map(AdTopic::getValue)
                .collect(Collectors.toSet());
    }
}
