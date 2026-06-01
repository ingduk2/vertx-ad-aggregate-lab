package com.ad.aggregate.lab.aggregate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AggregateQueryService {

    private final AggregateRepository aggregateRepository;

    public List<AdAggregate> findByDate(Long placementId, String date) {
        return aggregateRepository.findByPlacementIdAndDate(placementId, date);
    }

    public Optional<AdAggregate> findByHour(Long placementId, String date, String hour) {
        return aggregateRepository.findByPlacementIdAndDateAndHour(placementId, date, hour);
    }
}
