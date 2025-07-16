package org.example.stockcalculator.stock.profit;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.price.repository.StockPriceRepository;
import org.example.stockcalculator.repository.StockTransactionRepository;
import org.example.stockcalculator.transaction.service.UnsoldStockTransactionsService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockProfitService {

    private final StockTransactionRepository stockTransactionRepository;
    private final StockPriceRepository stockPriceRepository;
    private final UnsoldStockTransactionsService unsoldStockTransactionsService;

    public StockProfit calculateTotalProfit(Long userId) {
        Map<String, StockProfit> profitByStock = calculateProfitForSymbolWithInvestmentInfo(userId);
        return averageProfits(profitByStock.values());
    }

    public Map<String, StockProfit> calculateProfitForSymbolWithInvestmentInfo(Long userId) {
        List<Stock> stockSymbolsOfTransactions = stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(userId);
        return stockSymbolsOfTransactions.stream()
                .map(stock -> Map.entry(stock.getSymbol(), buildStockProfit(userId, stock.getSymbol())))
                .filter(entry -> entry.getValue().totalShares() > 0.0001)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private StockProfit buildStockProfit(Long userId, String stockSymbol) {
        List<StockTransaction> remainingBuys = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);
        StockPriceEntity currentPriceOfStock = stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(stockSymbol);

        double investedAmountInUsd = 0.0;
        double totalShares = 0.0;
        double currentValue = 0.0;
        double profit = 0.0;
        double profitPercentage = 0.0;
        double totalInvested = 0.0;

        if (currentPriceOfStock != null) {
            for (StockTransaction tx : remainingBuys) {
                double txInvested = tx.getPrice() * tx.getQuantity() + tx.getFee();
                investedAmountInUsd += txInvested;
                totalShares += tx.getQuantity();
                profit += (currentPriceOfStock.getPrice() - tx.getPrice()) * tx.getQuantity() - tx.getFee();
                totalInvested += tx.getPrice() * tx.getQuantity();
            }
            currentValue = currentPriceOfStock.getPrice() * totalShares;
            profitPercentage = totalInvested != 0 ? (profit / totalInvested) * 100 : 0.0;
        }
        return new StockProfit(profit, profitPercentage, currentValue, investedAmountInUsd, totalShares);
    }

    private StockProfit averageProfits(Collection<StockProfit> stockProfits) {
        double totalProfit = 0;
        double totalProfitPercent = 0;
        double totalCurrentValue = 0;
        double totalInvested = 0;
        double totalShares = 0;
        int count = stockProfits.size();
        for (StockProfit stockProfit : stockProfits) {
            totalProfit += stockProfit.profit();
            totalProfitPercent += stockProfit.profitPercentage();
            totalCurrentValue += stockProfit.currentValue();
            totalInvested += stockProfit.investedAmountInUsd();
            totalShares += stockProfit.totalShares();
        }
        double averageProfitPercent = count == 0 ? 0 : totalProfitPercent / count;
        return new StockProfit(
            totalProfit,
            averageProfitPercent,
            totalCurrentValue,
            totalInvested,
            totalShares
        );
    }
}
