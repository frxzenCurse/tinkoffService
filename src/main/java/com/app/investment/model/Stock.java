package com.app.investment.model;

import com.app.investment.enumeration.Currency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Stock {
    private String name;
    private String ticker;
    private Currency currency;
    private Integer lotSize;
    private BigDecimal price;
}
