package com.scb.trade.enrichment.service;

import com.scb.trade.enrichment.entity.Product;
import com.scb.trade.enrichment.entity.Trade;
import com.scb.trade.enrichment.entity.TradeEnriched;
import com.scb.trade.enrichment.repository.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EnrichmentServiceImplTest {

    private EnrichmentService service;
    @BeforeEach
    void setUp() {
        ProductRepo productRepo = mock(ProductRepo.class);
        Product tBillDomestic = Product.builder().id(1).name("Treasury Bills Domestic").build();
        Product corpBondDomestic = Product.builder().id(2).name("Corporate Bonds Domestic").build();
        Product repoDomestic = Product.builder().id(3).name("REPO Domestic").build();
        when(productRepo.getById(eq(1))).thenReturn(tBillDomestic);
        when(productRepo.getById(eq(2))).thenReturn(corpBondDomestic);
        when(productRepo.getById(eq(3))).thenReturn(repoDomestic);
        service = new EnrichmentServiceImpl(productRepo);
    }

    @Test
    void canEnrichValidTrades(){
        Trade trade_1 = Trade.builder().date("20160101").productId(1).currency("EUR").price(Double.valueOf("10.0")).build();
        Trade trade_2 = Trade.builder().date("20160101").productId(2).currency("EUR").price(Double.valueOf("20.0")).build();
        Trade trade_3 = Trade.builder().date("20160101").productId(3).currency("EUR").price(Double.valueOf("30.34")).build();
        List<TradeEnriched> enrichedTrades = service.enrich(List.of(trade_1, trade_2, trade_3));
        assertThat(enrichedTrades).isNotEmpty();
        assertThat(enrichedTrades.size()).isEqualTo(3);
        Optional<TradeEnriched> missingProdNameTrade = enrichedTrades.stream().filter(t -> "".equals(t.getProductName())).findAny();
        assertThat(missingProdNameTrade).isEmpty();
    }

    @Test
    void canHandleEmptyTrades(){
        List<TradeEnriched> enriched = service.enrich(Collections.emptyList());
        assertThat(enriched).isNotNull();
        assertThat(enriched).isEmpty();
    }

    @Test
    void canHandleMissingProduct(){
        Trade trade_11 = Trade.builder().date("20160101").productId(11).currency("EUR").price(Double.valueOf("35.34")).build();
        List<TradeEnriched> enriched = service.enrich(List.of(trade_11));
        assertThat(enriched).isNotNull();
        assertThat(enriched).isNotEmpty();
        assertThat(enriched.size()).isEqualTo(1);
        assertThat(enriched.get(0).getProductName()).isEqualTo("Missing Product Name");
    }
}
