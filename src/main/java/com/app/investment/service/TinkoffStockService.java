package com.app.investment.service;

import com.app.investment.enumeration.Currency;
import com.app.investment.exception.StocksNotFoundException;
import com.app.investment.model.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.InvestApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TinkoffStockService implements StockService {

    private final InvestApi api;

    @Override
    public ResponseEntity<List<Stock>> getStocks(List<String> stockNames) {
        var time = System.currentTimeMillis();
        var shares = api.getInstrumentsService().getAllShares();
        var list = shares.join();
        List<Stock> stocks = list.stream()
                .filter(item -> stockNames.contains(item.getName()))
                .map(item -> Stock.builder()
                        .name(item.getName())
                        .currency(Currency.valueOf(item.getCurrency().toUpperCase(Locale.ROOT)))
                        .ticker(item.getTicker())
                        .lotSize(item.getLot())
                        .price(getPrice(item.getFigi()))
                        .build())
                .filter(item -> item.getPrice().doubleValue() > 0)
                .collect(Collectors.toList());
        log.info("Get stocks - {}", System.currentTimeMillis() - time);

        if (stocks.isEmpty()) {
            throw new StocksNotFoundException("По вашему запросу, ничего не было найдено");
        }

        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }

    // think about async
    public BigDecimal getPrice(String figi) {
        log.info("Thread name - {}", Thread.currentThread().getName());
        try {
            int depth = 10;
            var orderBook = api.getMarketDataService().getOrderBookSync(figi, depth);

            return quotationToBigDecimal(orderBook.getLastPrice());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new BigDecimal(0);
        }
    }

    private BigDecimal quotationToBigDecimal(Quotation value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        long units = value.getUnits();
        int nanos = value.getNano();

        if (units == 0 && nanos == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(units).add(BigDecimal.valueOf(nanos, 9));
    }

    private int rowIndex = 0;
    private int labelColIndex = 0;
    private int valueColIndex = 1;

    @Override
    public ResponseEntity<byte[]> getStocksInXml(List<String> stockNames) throws IOException {
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet();

        List<Stock> stocks = getStocks(stockNames).getBody();

        for (int i = 0; i < 5; i++) {
            sheet.createRow(i);
        }

        for (Stock stock : stocks) {
            createRow(sheet, "Name", stock.getName(), rowIndex);
            createRow(sheet, "Ticker", stock.getTicker(), rowIndex);
            createRow(sheet, "Currency", stock.getCurrency().getCurrency(), rowIndex);
            createRow(sheet, "LotSize", stock.getLotSize().toString(), rowIndex);
            createRow(sheet, "Price", stock.getPrice().stripTrailingZeros().toPlainString(), rowIndex);

            rowIndex = 0;
            labelColIndex += 3;
            valueColIndex += 3;
        }

        var out = new ByteArrayOutputStream();

        wb.write(out);
        wb.close();
        out.close();

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    private void createRow(Sheet sheet, String label, String value, int index) {
        Row row = sheet.getRow(index);
        Cell cellLabel = row.createCell(labelColIndex);
        Cell cellValue = row.createCell(valueColIndex);

        cellLabel.setCellValue(label);
        cellValue.setCellValue(value);

        rowIndex++;
    }
}
