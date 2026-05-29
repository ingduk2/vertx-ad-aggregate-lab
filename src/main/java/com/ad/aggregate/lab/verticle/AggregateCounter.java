package com.ad.aggregate.lab.verticle;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class AggregateCounter {
    private final Long placementId;
    private final String date;
    private final String hour;
    private long request;
    private long impression;
    private long click;

    private AggregateCounter(Long placementId, String date, String hour) {
        this.placementId = Objects.requireNonNull(placementId);
        this.date = Objects.requireNonNull(date);
        this.hour = Objects.requireNonNull(hour);
    }

    public static AggregateCounter create(Long placementId, String date, String hour) {
        return new AggregateCounter(placementId, date, hour);
    }

    public void addRequest(long count) {
        request += count;
    }

    public void addImpression(long count) {
        impression += count;
    }

    public void addClick(long count) {
        click += count;
    }
}
