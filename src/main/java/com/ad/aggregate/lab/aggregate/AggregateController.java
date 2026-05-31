package com.ad.aggregate.lab.aggregate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/aggregate")
@RequiredArgsConstructor
public class AggregateController {

    private final AggregateService aggregateService;

    @GetMapping("/{placementId}/{date}")
    public List<AdAggregateResponse> getByDate(
            @PathVariable Long placementId,
            @PathVariable String date
    ) {
        List<AdAggregate> adAggregates = aggregateService.findByDate(placementId, date);

        return adAggregates.stream()
                .map(AdAggregateResponse::from)
                .toList();
    }

    @GetMapping("/{placementId}/{date}/{hour}")
    public ResponseEntity<AdAggregateResponse> getByHour(
            @PathVariable Long placementId,
            @PathVariable String date,
            @PathVariable String hour
    ) {
        Optional<AdAggregate> adAggregate = aggregateService.findByHour(placementId, date, hour);

        return adAggregate
                .map(AdAggregateResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buffer/snapshot")
    public CompletableFuture<List<AdAggregateResponse>> getBufferSnapshot() {
        return aggregateService.getBufferSnapshot();
    }
}
