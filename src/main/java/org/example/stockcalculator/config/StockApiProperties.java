package org.example.stockcalculator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stock.prices-api")
public record StockApiProperties(String url, String apiKey) {
}
