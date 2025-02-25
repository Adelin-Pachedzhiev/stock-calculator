package org.example.stockcalculator.service;

import org.example.stockcalculator.model.StockPrice;
import org.example.stockcalculator.model.StockProfit;
import org.springframework.stereotype.Component;

@Component
public class StockPriceCalculator {

    public StockProfit calculate(StockPrice stockPrice, StockPrice otherStockPrice) {
        double profit = stockPrice.currentPrice() - otherStockPrice.currentPrice();
        double profitPercentage = (profit / otherStockPrice.currentPrice()) * 100;
        return new StockProfit(profit, profitPercentage);
    }
}
