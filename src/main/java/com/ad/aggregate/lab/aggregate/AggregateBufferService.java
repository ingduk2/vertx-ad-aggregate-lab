package com.ad.aggregate.lab.aggregate;

import com.ad.aggregate.lab.common.EventBusAddress;
import com.ad.aggregate.lab.verticle.model.AggregateCounter;
import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AggregateBufferService {

    private final Vertx vertx;
    private final ObjectMapper objectMapper;

    public Mono<List<AdAggregateResponse>> getBufferSnapshot() {
        return Mono.create(sink ->
                vertx.eventBus().<String>request(EventBusAddress.AGGREGATE_BUFFER_SNAPSHOT.address(), "")
                        .onSuccess(reply -> {
                            List<AggregateCounter> counters = objectMapper.readValue(
                                    reply.body(),
                                    new TypeReference<List<AggregateCounter>>() {
                                    }
                            );
                            sink.success(counters.stream()
                                    .map(AdAggregateResponse::fromCounter)
                                    .toList());
                        })
                        .onFailure(sink::error)
        );
    }
}
