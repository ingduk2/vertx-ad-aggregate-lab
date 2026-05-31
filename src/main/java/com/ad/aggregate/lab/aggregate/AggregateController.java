package com.ad.aggregate.lab.aggregate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/aggregate")
@RequiredArgsConstructor
public class AggregateController {

    private final AggregateService aggregateService;

    @GetMapping("/{placementId}/{date}")
    public Flux<AdAggregateResponse> getByDate(
            @PathVariable Long placementId,
            @PathVariable String date
    ) {
        List<AdAggregate> adAggregates = aggregateService.findByDate(placementId, date);

        List<AdAggregateResponse> responses = adAggregates.stream()
                .map(AdAggregateResponse::from)
                .toList();
        
        return Flux.fromIterable(responses);
    }

    @GetMapping("/{placementId}/{date}/{hour}")
    public Mono<ResponseEntity<AdAggregateResponse>> getByHour(
            @PathVariable Long placementId,
            @PathVariable String date,
            @PathVariable String hour
    ) {
        Optional<AdAggregate> adAggregate = aggregateService.findByHour(placementId, date, hour);

        AdAggregateResponse response = adAggregate
                .map(AdAggregateResponse::from)
                .orElse(null);

        return Mono.justOrEmpty(response)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/buffer/snapshot")
    public Mono<List<AdAggregateResponse>> getBufferSnapshot() {
        return aggregateService.getBufferSnapshot();
    }
}
