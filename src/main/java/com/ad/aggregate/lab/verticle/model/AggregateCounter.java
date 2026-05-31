package com.ad.aggregate.lab.verticle.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonCreator
    private AggregateCounter(
            @JsonProperty("placementId") Long placementId,
            @JsonProperty("date") String date,
            @JsonProperty("hour") String hour
    ) {
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
