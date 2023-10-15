package com.scb.trade.enrichment.entity;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Builder
@RequiredArgsConstructor
@Value
public class Trade {
    private final String date;
    private final int productId;
    private final String currency;
    private final Double price;
}
