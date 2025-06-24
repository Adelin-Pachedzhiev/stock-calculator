package org.example.stockcalculator.portfolio.dto;

public record PortfolioOverview(
    double totalProfit,
    double totalProfitPercentage,
    double totalInvestmentCost,
    double currentMarketValue
) {} 
