package com.ad.aggregate.lab.aggregate;

import com.ad.aggregate.lab.common.EventBusAddress;
import com.ad.aggregate.lab.verticle.model.AggregateCounter;
import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AggregateService {

    private final AggregateRepository aggregateRepository;
    private final JdbcTemplate jdbcTemplate;
    private final Vertx vertx;
    private final ObjectMapper objectMapper;

    @Transactional
    public void upsert(
            Long placementId1,
            Long placementId2,
            String date,
            String hour
    ) {
        aggregateRepository.upsert(placementId1, date, hour, 1L, 1L, 0L);
        aggregateRepository.upsert(placementId2, date, hour, 1L, 1L, 0L);
    }

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

    public List<AdAggregate> findByDate(Long placementId, String date) {
        return aggregateRepository.findByPlacementIdAndDate(placementId, date);
    }

    public Optional<AdAggregate> findByHour(Long placementId, String date, String hour) {
        return aggregateRepository.findByPlacementIdAndDateAndHour(placementId, date, hour);
    }

    public CompletableFuture<List<AdAggregateResponse>> getBufferSnapshot() {
        CompletableFuture<List<AdAggregateResponse>> future = new CompletableFuture<>();

        vertx.eventBus().<String>request(EventBusAddress.AGGREGATE_BUFFER_SNAPSHOT.address(), "")
                .onSuccess(reply -> {
                    List<AggregateCounter> counters = objectMapper.readValue(
                            reply.body(),
                            new TypeReference<List<AggregateCounter>>() {
                            }
                    );
                    future.complete(counters.stream()
                            .map(AdAggregateResponse::fromCounter)
                            .toList());
                }).onFailure(future::completeExceptionally);

        return future;
    }
}
