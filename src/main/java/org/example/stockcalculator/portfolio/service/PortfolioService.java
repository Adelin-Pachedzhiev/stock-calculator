package org.example.stockcalculator.portfolio.service;

import org.example.stockcalculator.portfolio.dto.PortfolioOverview;
import org.example.stockcalculator.stock.profit.StockProfit;
import org.example.stockcalculator.stock.profit.StockProfitService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final StockProfitService stockProfitService;
    private final StockInvestmentValueService stockInvestmentValueService;

    public PortfolioOverview getPortfolioOverview(Long userId) {
        StockProfit totalProfit = stockProfitService.calculateTotalProfit(userId);
        Double totalInvestmentCost = stockInvestmentValueService.calculateTotalInvestmentValue(userId);

        double currentMarketValue = totalInvestmentCost + totalProfit.profit();
        
        return new PortfolioOverview(
                totalProfit.profit(),
                totalProfit.profitPercentage(),
                totalInvestmentCost,
                currentMarketValue
        );
    }
} 
