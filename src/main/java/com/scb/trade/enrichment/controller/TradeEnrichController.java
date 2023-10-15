package com.scb.trade.enrichment.controller;

import com.scb.trade.enrichment.entity.Trade;
import com.scb.trade.enrichment.entity.TradeEnriched;
import com.scb.trade.enrichment.service.EnrichmentService;
import com.scb.trade.enrichment.util.CsvHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class TradeEnrichController {
    @Autowired
    private final EnrichmentService enrichmentService;
    @PostMapping(path = "/enrich",
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_OCTET_STREAM_VALUE},
        produces = "text/csv"
    )
    ResponseEntity<String> enrichTrades(@RequestParam("file") MultipartFile resource){
        log.info("Enrich trades request received");
        if(CsvHandler.isValidCSVFile(resource)){
            try {
                List<Trade> trades = CsvHandler.parseTrade(resource.getInputStream());
                List<TradeEnriched> enriched = enrichmentService.enrich(trades);
                String csvResponse = CsvHandler.generateCsv(enriched);
                return ResponseEntity.accepted().body(csvResponse);
            } catch (Exception e) {
                log.error("An exception was thrown when processing the request received ",e);
                return ResponseEntity.badRequest().body("Unable to process trade file sent for enrichment");
            }
        }else {
            log.warn("Request received does not contain a valid CSV payload, {}",resource.getContentType());
            return ResponseEntity.badRequest().body("No valid csv payload received");
        }

    }
}
