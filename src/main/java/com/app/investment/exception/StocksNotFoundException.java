package com.app.investment.exception;

public class StocksNotFoundException extends RuntimeException {
    public StocksNotFoundException(String message) {
        super(message);
    }
}
