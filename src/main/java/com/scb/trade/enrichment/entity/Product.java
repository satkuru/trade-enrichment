package com.scb.trade.enrichment.entity;

import lombok.*;

@Value
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class Product {
    private final Integer id;
    private final String name;
}
