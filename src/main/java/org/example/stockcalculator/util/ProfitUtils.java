package org.example.stockcalculator.util;

import java.util.Collection;

import org.example.stockcalculator.model.StockProfit;

public class ProfitUtils {

    public static StockProfit averageProfits(Collection<StockProfit> stockProfits) {
        double totalProfit = 0;
        double totalProfitPercent = 0;
        for (StockProfit stockProfit : stockProfits) {
            totalProfit += stockProfit.profit();
            totalProfitPercent += stockProfit.profitPercentage();
        }
        double averageProfitPercent = stockProfits.isEmpty() ? 0 : totalProfitPercent / stockProfits.size();

        return new StockProfit(totalProfit, averageProfitPercent);
    }

}
