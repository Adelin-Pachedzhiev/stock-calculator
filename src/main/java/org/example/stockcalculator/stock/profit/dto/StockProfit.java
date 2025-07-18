package org.example.stockcalculator.stock.profit.dto;

public record StockProfit(
        double profit,
        double profitPercentage,
        double currentValue,
        double investedAmountInUsd,
        double totalShares) {
}
