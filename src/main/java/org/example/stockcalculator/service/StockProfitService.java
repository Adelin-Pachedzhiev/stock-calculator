package org.example.stockcalculator.service;

import static java.util.stream.Collectors.toMap;
import static org.example.stockcalculator.entity.TransactionType.BUY;
import static org.example.stockcalculator.entity.TransactionType.SELL;
import static org.example.stockcalculator.util.ProfitUtils.averageProfits;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.entity.StockTransaction;
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

    public Map<String, StockProfit> calculateProfitPerStock(Long userId) {
        List<String> stockSymbolsOfTransactions = stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(userId);
        return stockSymbolsOfTransactions.stream()
                .collect(toMap(Function.identity(), symbol -> calculateForUserAndSymbol(userId, symbol)));
    }

    public StockProfit calculateTotalProfit(Long userId) {
        Map<String, StockProfit> profitByStock = calculateProfitPerStock(userId);
        return averageProfits(profitByStock.values());
    }

    private StockProfit calculateForUserAndSymbol(Long userId, String stockSymbol) {
        List<StockTransaction> transactionsForStock = stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimestampAsc(userId, stockSymbol);

        List<StockTransaction> remainingBuys = new ArrayList<>();

        for (StockTransaction tx : transactionsForStock) {
            if (tx.getType().equals(BUY)) {
                remainingBuys.add(tx);
            }
            else if (tx.getType().equals(SELL)) {
                matchSellWithBuyTransactions(tx, remainingBuys);
            }
            else {
                throw new IllegalArgumentException("Unknown transaction type: " + tx.getType());
            }
        }

        return calculateProfitForTransactions(stockSymbol, remainingBuys);
    }

    private void matchSellWithBuyTransactions(StockTransaction tx, List<StockTransaction> remainingBuys) {
        double quantityToSell = tx.getQuantity();

        Iterator<StockTransaction> iterator = remainingBuys.iterator();
        while (iterator.hasNext() && quantityToSell > 0) {
            StockTransaction buyTx = iterator.next();
            double buyQty = buyTx.getQuantity();

            if (buyQty <= quantityToSell) {
                quantityToSell -= buyQty;
                iterator.remove();
            }
            else {
                buyTx.setQuantity(buyQty - quantityToSell);
                quantityToSell = 0;
            }
        }
    }

    private StockProfit calculateProfitForTransactions(String stockSymbol, List<StockTransaction> transactions) {
        StockPriceEntity currentPriceOfStock = stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(stockSymbol);

        List<StockProfit> profits = transactions.stream()
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
