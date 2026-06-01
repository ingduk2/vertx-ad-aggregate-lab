package com.ad.aggregate.lab.aggregate;

import com.ad.aggregate.lab.verticle.model.AggregateCounter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AggregateBulkService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void bulkUpsert(Map<String, AggregateCounter> buffer) {
        List<Object[]> batchArgs = buffer.values().stream()
                .map(counter -> new Object[]{
                        counter.getPlacementId(),
                        counter.getDate(),
                        counter.getHour(),
                        counter.getRequest(),
                        counter.getImpression(),
                        counter.getClick()
                })
                .toList();

        jdbcTemplate.batchUpdate("""
                INSERT INTO ad_aggregate (placement_id, date, hour, request_count, impression_count, click_count)
                VALUES (?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    request_count = request_count + VALUES(request_count),
                    impression_count = impression_count + VALUES(impression_count),
                    click_count = click_count + VALUES(click_count)
                """, batchArgs);
    }
}
