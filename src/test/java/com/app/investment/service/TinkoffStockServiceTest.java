package com.app.investment.service;

import com.app.investment.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InvestApi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TinkoffStockServiceTest {
    @Mock
    private InvestApi investApi;

    TinkoffStockService tinkoffStockService;

    private final static String NAME = "test";
    private final static String TICKER = "FXTEST";
    private final static String CURRENCY = "rub";
    private final static int LOT = 10;
    private final static BigDecimal PRICE = new BigDecimal(123);

    @BeforeEach
    void beforeEach() {
        CompletableFuture<List<Share>> futureList = new CompletableFuture<>();
        List<Share> list = new ArrayList<>();
        Share share = Share.newBuilder()
                .setName(NAME)
                .setTicker(TICKER)
                .setCurrency(CURRENCY)
                .setLot(LOT)
                .setFigi("QWEASDZXC")
                .build();
        list.add(share);
        futureList.complete(list);

        when(investApi.getInstrumentsService().getAllShares())
                .thenReturn(futureList);
        when(tinkoffStockService.getPrice(any()))
                .thenReturn(PRICE);

        tinkoffStockService = new TinkoffStockService(investApi);
    }

    @Test
    void getStocks() {
        List<String> list = new ArrayList<>();
        list.add("qwe");
        Stock actualStock = tinkoffStockService.getStocks(list).getBody().get(0);

        assertEquals(NAME, actualStock.getName());
        assertEquals(TICKER, actualStock.getTicker());
        assertEquals(CURRENCY, actualStock.getCurrency().getCurrency());
        assertEquals(LOT, actualStock.getLotSize());
        assertEquals(PRICE, actualStock.getPrice());

        verify(investApi, times(1)).getInstrumentsService();
        verify(investApi, times(1)).getMarketDataService();
    }

}
