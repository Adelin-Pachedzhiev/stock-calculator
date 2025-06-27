package org.example.stockcalculator.stock.profit;

import static java.util.stream.Collectors.toMap;
import static org.example.stockcalculator.util.ProfitUtils.averageProfits;

import java.util.List;
import java.util.Map;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.repository.StockPriceRepository;
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

    public Map<String, StockProfit> calculateProfitPerStock(Long userId) {
        List<Stock> stockSymbolsOfTransactions = stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(userId);
        return stockSymbolsOfTransactions.stream()
                .collect(toMap(Stock::getSymbol, stock -> calculateProfitForTransactions(userId, stock.getSymbol())));
    }

    public StockProfit calculateTotalProfit(Long userId) {
        Map<String, StockProfit> profitByStock = calculateProfitPerStock(userId);
        return averageProfits(profitByStock.values());
    }

    private StockProfit calculateProfitForTransactions(Long userId, String stockSymbol) {
        List<StockTransaction> remainingBuys = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);
        StockPriceEntity currentPriceOfStock = stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(stockSymbol);

        List<StockProfit> profits = remainingBuys.stream()
                .map(tx -> calculateProfitForSingleTransaction(tx, currentPriceOfStock))
                .toList();

        return averageProfits(profits);
    }

    private StockProfit calculateProfitForSingleTransaction(StockTransaction transaction, StockPriceEntity currentPriceOfStock) {
        double priceDifference = currentPriceOfStock.getPrice() - transaction.getPrice();
        double profit = (priceDifference * transaction.getQuantity()) - transaction.getFee();
        double profitPercentage = (priceDifference / transaction.getPrice()) * 100;

        return new StockProfit(profit, profitPercentage);
    }
}
