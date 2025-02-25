package org.example.stockcalculator.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockcalculator.model.StockPrice;
import org.example.stockcalculator.model.StockProfit;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestCalculator {

    private final StockPriceRetriever stockPriceRetriever;
    private final StockPriceCalculator stockPriceCalculator;

    @PostConstruct
    public void test() {
        StockPrice aapl = stockPriceRetriever.retrieve("AAPL").orElseThrow();
        StockPrice oldStockPrice = new StockPrice(244.87);

        StockProfit calculate = stockPriceCalculator.calculate(aapl, oldStockPrice);
        log.info("Profit: {}, Profit percentage: {}", calculate.profit(), calculate.profitPercentage());
    }
}
