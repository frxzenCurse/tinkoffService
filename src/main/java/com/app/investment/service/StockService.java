package com.app.investment.service;

import com.app.investment.model.Stock;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface StockService {
    ResponseEntity<List<Stock>> getStocks(List<String> stockNames);
    ResponseEntity<byte[]> getStocksInXml(List<String> stockNames) throws IOException;
}
