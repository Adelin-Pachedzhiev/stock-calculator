package org.example.stockcalculator.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.model.StockPrice;
import org.example.stockcalculator.model.StockProfit;
import org.example.stockcalculator.repository.StockPriceRepository;
import org.example.stockcalculator.repository.StockTransactionRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockProfitService {

    private final StockTransactionRepository stockTransactionRepository;
    private final StockPriceRepository stockPriceRepository;

    public StockProfit calculate(StockPrice stockPrice, StockPrice otherStockPrice) {
        double profit = stockPrice.currentPrice() - otherStockPrice.currentPrice();
        double profitPercentage = (profit / otherStockPrice.currentPrice()) * 100;
        return new StockProfit(profit, profitPercentage);
    }

    public Map<String, StockProfit> calculate(Long userId) {
        Map<String, List<StockTransaction>> transactionsForUser = stockTransactionRepository.findByUserIdGroupedByStockSymbol(userId);

        return transactionsForUser.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> calculateProfitForAllTransactions(entry.getKey(), entry.getValue())));
    }

    private StockProfit calculateProfitForAllTransactions(String stockSymbol, List<StockTransaction> transactions) {
        StockPriceEntity currentPriceOfStock = stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(stockSymbol);

        return transactions.stream()
                .map(transaction -> calculateProfit(transaction, currentPriceOfStock))
                .reduce(StockProfit::sum)
                .orElse(new StockProfit());
    }

    private StockProfit calculateProfit(StockTransaction transaction, StockPriceEntity currentPriceOfStock) {
        double priceDifference = transaction.getPrice() - currentPriceOfStock.getPrice();
        double profit = (priceDifference * transaction.getQuantity()) - transaction.getFee();
        double profitPercentage = (priceDifference / transaction.getPrice()) * 100;
        return new StockProfit(profit, profitPercentage);
    }
}
