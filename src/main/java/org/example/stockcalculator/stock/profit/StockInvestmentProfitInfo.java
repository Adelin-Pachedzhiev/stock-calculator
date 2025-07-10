package org.example.stockcalculator.stock.profit;

import org.example.stockcalculator.entity.Stock;

public record StockInvestmentProfitInfo(
    Stock stock,
    StockProfit stockProfit
) {}
