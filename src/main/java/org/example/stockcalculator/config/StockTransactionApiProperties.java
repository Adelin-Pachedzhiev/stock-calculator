package org.example.stockcalculator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stock.transactions-api")
public record StockTransactionApiProperties(
        String url
) {}
