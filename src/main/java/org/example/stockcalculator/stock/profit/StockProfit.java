package org.example.stockcalculator.stock.profit;

public record StockProfit(
        double profit,
        double profitPercentage,
        double currentValue,
        double investedAmountInUsd,
        double totalShares) {
}
