package com.app.investment.controller;

import com.app.investment.model.Stock;
import com.app.investment.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;


    @PostMapping("/")
    public ResponseEntity<List<Stock>> getStocks(@RequestBody List<String> stockNames) {
        return stockService.getStocks(stockNames);
    }

    @PostMapping("/xlsx")
    public ResponseEntity<byte[]> getStocksInXml(@RequestBody List<String> stockNames) throws IOException {
        return stockService.getStocksInXml(stockNames);
    }
}
