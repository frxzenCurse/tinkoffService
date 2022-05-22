package com.app.investment.controller;

import com.app.investment.enumeration.Currency;
import com.app.investment.model.Stock;
import com.app.investment.service.StockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
public class StockControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    StockService stockService;

    private static final String NAME = "test";
    private static final String TICKER = "TFTEST";
    private static final Currency CURRENCY = Currency.EUR;
    private static final Integer LOT_SIZE = 10;
    private static final BigDecimal PRICE = BigDecimal.TEN;
    Stock stock = Stock.builder()
            .name(NAME)
            .currency(CURRENCY)
            .ticker(TICKER)
            .lotSize(LOT_SIZE)
            .price(PRICE)
            .build();
    List<Stock> list = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        list.add(stock);

        when(stockService.getStocks(any())).thenReturn(new ResponseEntity<>(list, HttpStatus.OK));
    }

    @Test
    void getStock() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> request = new ArrayList<>();
        request.add("Qwe");


        String result = mockMvc.perform(
                post("/api/v1/stocks/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        verify(stockService, times(1)).getStocks(anyList());
        assertEquals(result, objectMapper.writeValueAsString(list));
    }
}
