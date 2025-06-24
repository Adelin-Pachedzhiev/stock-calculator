package org.example.stockcalculator.dto;

public record StockInformationResponse(
        Long stockId,
        String symbol,
        String name,
        String description,
        double currentPrice,
        double change,
        double changePercent
) {

}
