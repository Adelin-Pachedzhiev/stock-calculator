package org.example.stockcalculator.model;

public record PortfolioOverview(
    double totalProfit,
    double totalProfitPercentage,
    double totalInvestmentCost,
    double currentMarketValue
) {} 