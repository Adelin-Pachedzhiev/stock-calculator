package org.example.stockcalculator.stock.profit.dto;

import org.example.stockcalculator.entity.Stock;

public record StockInvestmentProfitInfo(
    Stock stock,
    StockProfit stockProfit
) {}
