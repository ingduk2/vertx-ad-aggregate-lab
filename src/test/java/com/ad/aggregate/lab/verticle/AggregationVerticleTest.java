package com.ad.aggregate.lab.verticle;

import com.ad.aggregate.lab.common.AdTopic;
import com.ad.aggregate.lab.common.EventBusAddress;
import com.ad.aggregate.lab.config.AggregateProperties;
import com.ad.aggregate.lab.verticle.model.AggregateCounter;
import com.ad.aggregate.lab.verticle.model.FlushPayload;
import com.ad.aggregate.lab.verticle.model.FlushPayloadCodec;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
class AggregationVerticleTest {

    @BeforeEach
    void deploy(Vertx vertx, VertxTestContext testContext) {
        ObjectMapper objectMapper = new ObjectMapper();
        AggregateProperties aggregateProperties = new AggregateProperties(5, 1000);

        vertx.eventBus().registerDefaultCodec(FlushPayload.class, new FlushPayloadCodec());

        vertx.deployVerticle(new AggregationVerticle(objectMapper, aggregateProperties))
                .onSuccess(id -> testContext.completeNow())
                .onFailure(testContext::failNow);
    }

    @Test
    @DisplayName("집계 테스트")
    void test1(Vertx vertx, VertxTestContext testContext) {
        String body = """
                {"base":{"placementId":10881244,"date":"20260601","hour":"10","count":1}}
                """;

        for (int i = 0; i < 5; i++) {
            vertx.eventBus().publish(AdTopic.AD_REQUEST.getValue(), body);
            vertx.eventBus().publish(AdTopic.AD_IMPRESSION.getValue(), body);
            vertx.eventBus().publish(AdTopic.AD_CLICK.getValue(), body);
        }

        vertx.setTimer(500, id ->
                vertx.eventBus().<String>request(EventBusAddress.AGGREGATE_BUFFER_SNAPSHOT.address(), "")
                        .onSuccess(reply -> {
                            testContext.verify(() -> {
                                ObjectMapper objectMapper = new ObjectMapper();
                                List<AggregateCounter> counters = objectMapper.readValue(
                                        reply.body(),
                                        new TypeReference<>() {}
                                );
                                assertThat(counters).hasSize(1);
                                assertThat(counters.getFirst().getPlacementId()).isEqualTo(10881244L);
                                assertThat(counters.getFirst().getRequest()).isEqualTo(5L);
                                assertThat(counters.getFirst().getImpression()).isEqualTo(5L);
                                assertThat(counters.getFirst().getClick()).isEqualTo(5L);
                            });
                            testContext.completeNow();
                        })
                        .onFailure(testContext::failNow)
        );
    }
}