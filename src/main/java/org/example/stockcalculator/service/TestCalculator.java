package org.example.stockcalculator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockcalculator.model.StockPrice;
import org.example.stockcalculator.model.StockProfit;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestCalculator {

    private final StockPriceApiClient stockPriceApiClient;
    private final StockPriceCalculator stockPriceCalculator;
    private final JdbcTemplate jdbcTemplate;

    //    @Scheduled(fixedRate = 6, timeUnit = TimeUnit.SECONDS)
    public void test() {
        for (int i = 0; i < 5; i++) {

            StockPrice aapl = stockPriceApiClient.getPriceForSymbol("AAPL").orElseThrow();
            StockPrice oldStockPrice = new StockPrice(244.87);

            StockProfit calculate = stockPriceCalculator.calculate(aapl, oldStockPrice);
            log.info("Profit: {}, Profit percentage: {}", calculate.profit(), calculate.profitPercentage());
            jdbcTemplate.execute("SELECT 1");
        }
    }
}
