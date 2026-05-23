package com.ad.aggregate.lab.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(
        name = "ad_aggregate",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"placement_id", "date", "hour"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "placement_id", nullable = false)
    private Long placementId;

    @Column(name = "date", nullable = false)
    private String date; // yyyyMMdd

    @Column(name = "hour", nullable = false)
    private String hour; // HH

    @Column(name = "request_count")
    private Long requestCount = 0L;

    @Column(name = "impression_count")
    private Long impressionCount = 0L;

    @Column(name = "click_count")
    private Long clickCount = 0L;

    public static AdAggregate create(Long placementId, String date, String hour) {
        AdAggregate adAggregate = new AdAggregate();
        adAggregate.placementId = Objects.requireNonNull(placementId);
        adAggregate.date = Objects.requireNonNull(date);
        adAggregate.hour = Objects.requireNonNull(hour);
        return adAggregate;
    }

    public void addRequest(long count) {
        this.requestCount += count;
    }

    public void addImpression(long count) {
        this.impressionCount += count;
    }

    public void addClick(long count) {
        this.clickCount += count;
    }
}
