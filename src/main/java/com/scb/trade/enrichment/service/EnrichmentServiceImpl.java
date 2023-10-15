package com.scb.trade.enrichment.service;

import com.scb.trade.enrichment.entity.Trade;
import com.scb.trade.enrichment.entity.TradeEnriched;
import com.scb.trade.enrichment.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class EnrichmentServiceImpl implements EnrichmentService {
    private final ProductRepo productRepo;
    @Override
    public List<TradeEnriched> enrich(List<Trade> tradeInputs) {
        if(Objects.isNull(tradeInputs)||tradeInputs.isEmpty()){
            log.warn("Empty trades list passed for enrichment");
            return Collections.emptyList();
        }
        List<TradeEnriched> tradeEnricheds = tradeInputs
            .stream()
            .filter(Objects::nonNull)
            .map(this::updateProductName)
            .collect(Collectors.toList());
        return tradeEnricheds;
    }

    private TradeEnriched updateProductName(Trade input){
        var product = productRepo.getById(input.getProductId());
        TradeEnriched.TradeEnrichedBuilder enrichedBuilder = TradeEnriched.builder()
            .date(input.getDate())
            .currency(input.getCurrency())
            .price(input.getPrice());
        if(Objects.isNull(product)){
            enrichedBuilder.productName("Missing Product Name");
        }else {
            enrichedBuilder.productName(product.getName());
        };
        return enrichedBuilder.build();
    }
}
