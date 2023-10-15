package com.scb.trade.enrichment.entity;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TradeEnriched {
    private final String date;
    private final String productName;
    private final String currency;
    private final Double price;
}
