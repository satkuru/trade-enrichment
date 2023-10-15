package com.scb.trade.enrichment.util;

import com.scb.trade.enrichment.entity.Trade;
import com.scb.trade.enrichment.entity.TradeEnriched;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class CsvHandler {
    public static String TYPE = "application/octet-stream";
    public static boolean isValidCSVFile(MultipartFile file){
        return TYPE.equals(file.getContentType());
    }

    public static List<Trade> parseTrade(InputStream tradeFileStream) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(tradeFileStream, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                 CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
            return csvParser.getRecords().stream().map(CsvHandler::asTrade).filter(Objects::nonNull).collect(Collectors.toList());
        }catch (IOException iox){
            log.error("An exception was thrown when parsing the incoming csv files");
        }
        return Collections.emptyList();
    }

    private static Trade asTrade(CSVRecord record){
        Trade trade = null;
        try {
            //handle any malformed csv record which cannot be parsed into a trade object
            trade = Trade.builder()
                .date(record.get("date"))
                .productId(Integer.valueOf(record.get("product_id")))
                .currency(record.get("currency"))
                .price(Double.valueOf(record.get("price")))
                .build();
        }catch (Exception ex){
            log.error("Unable to parse this record, appears to be malformed {}",record);
        }
         return trade;
    }

    public static String generateCsv(List<TradeEnriched> trades){
        try(StringWriter stringWriter = new StringWriter();
            BufferedWriter bwritter = new BufferedWriter(stringWriter);
            CSVPrinter csvPrinter = new CSVPrinter(bwritter, CSVFormat.DEFAULT
                .withHeader("date","product_name","currency","price"))){
            for (TradeEnriched trade :trades) {
                writeCsv(trade,csvPrinter);
            }
            csvPrinter.flush();
            return stringWriter.getBuffer().toString();
        } catch (IOException e) {
            throw new RuntimeException("An exception was thrown while writing csv response", e);
        }
    }

    private static void writeCsv(TradeEnriched trade, CSVPrinter printer) throws IOException {
        printer.printRecord(trade.getDate(),trade.getProductName(),trade.getCurrency(),trade.getPrice());
    }
}
