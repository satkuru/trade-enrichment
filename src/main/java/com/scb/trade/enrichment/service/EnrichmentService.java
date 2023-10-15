package com.scb.trade.enrichment.service;

import com.scb.trade.enrichment.entity.TradeEnriched;
import com.scb.trade.enrichment.entity.Trade;

import java.util.List;

public interface EnrichmentService {
    List<TradeEnriched> enrich(List<Trade> tradeInputs);
}
