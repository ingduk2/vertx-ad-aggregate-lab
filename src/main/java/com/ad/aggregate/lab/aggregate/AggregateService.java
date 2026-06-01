package com.ad.aggregate.lab.aggregate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AggregateService {

    private final AggregateRepository aggregateRepository;

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
}
