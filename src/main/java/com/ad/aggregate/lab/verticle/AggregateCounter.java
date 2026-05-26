package com.ad.aggregate.lab.verticle;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AggregateCounter {
    private long request;
    private long impression;
    private long click;

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
