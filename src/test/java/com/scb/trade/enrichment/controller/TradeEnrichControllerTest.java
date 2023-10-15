package com.scb.trade.enrichment.controller;

import com.scb.trade.enrichment.entity.TradeEnriched;
import com.scb.trade.enrichment.service.EnrichmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(locations = "classpath:application.properties")
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = TradeEnrichController.class)
class TradeEnrichControllerTest {

    public static final String TRADE_FILE_CONTENT = """
        date,product_id,currency,price
        20160101,1,EUR,10.0
        """;
    public static final String EXPECTED_ENRICHED_TRADES_RESPONSE = "date,product_name,currency,price\n"+
  "20160101,Treasury Bills Domestic,EUR,10.0\n";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private EnrichmentService enrichmentService;


    @BeforeEach
    void setUp() {
    }

    @Test
    void canEnrichTradeRequest() throws Exception {
        List<TradeEnriched> enrichedList = List.of(TradeEnriched.builder()
            .date("20160101")
            .productName("Treasury Bills Domestic")
            .price(Double.valueOf("10"))
            .currency("EUR")
            .build());
        when(enrichmentService.enrich(anyList())).thenReturn(enrichedList);
        MockMultipartFile file
            = new MockMultipartFile(
            "file",
            "trade.csv",
            MediaType.APPLICATION_OCTET_STREAM.toString(),
            TRADE_FILE_CONTENT.getBytes()
        );

        MockMvc mockMvc
            = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult mvcResult = mockMvc.perform(multipart("/api/v1/enrich").file(file))
            .andExpect(status().isAccepted())
            .andReturn();
        assertThat(mvcResult.getResponse().getContentAsString().stripIndent()).isEqualTo(EXPECTED_ENRICHED_TRADES_RESPONSE.stripIndent());
    }
}
