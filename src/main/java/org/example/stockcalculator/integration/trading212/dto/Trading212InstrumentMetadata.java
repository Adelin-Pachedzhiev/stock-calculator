package org.example.stockcalculator.integration.trading212.dto;

public record Trading212InstrumentMetadata(
        String ticker,
        String type,
        String name,
        String shortName,
        String currencyCode,
        String isin) {

}
