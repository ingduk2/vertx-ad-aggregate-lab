package com.ad.aggregate.lab.aggregate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AggregateRepository extends JpaRepository<AdAggregate, Long> {
    Optional<AdAggregate> findByPlacementIdAndDateAndHour(Long placementId, String date, String hour);

    List<AdAggregate> findByPlacementIdAndDate(Long placementId, String date);

    @Modifying
    @Query(value = """
        INSERT INTO ad_aggregate (placement_id, date, hour, request_count, impression_count, click_count)
        VALUES (:placementId, :date, :hour, :requestCount, :impressionCount, :clickCount)
        ON DUPLICATE KEY UPDATE
            request_count = request_count + VALUES(request_count),
            impression_count = impression_count + VALUES(impression_count),
            click_count = click_count + VALUES(click_count)
        """, nativeQuery = true)
    void upsert(@Param("placementId") Long placementId,
                @Param("date") String date,
                @Param("hour") String hour,
                @Param("requestCount") long requestCount,
                @Param("impressionCount") long impressionCount,
                @Param("clickCount") long clickCount);
}
