package org.example.stockcalculator.service;

import static java.util.stream.Collectors.toMap;
import static org.example.stockcalculator.entity.TransactionType.BUY;
import static org.example.stockcalculator.entity.TransactionType.SELL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockInvestmentValueService {

    private final StockTransactionRepository stockTransactionRepository;

    public Map<String, Double> calculateInvestmentValuePerStock(Long userId) {
        List<String> stockSymbolsOfTransactions = stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(userId);
        return stockSymbolsOfTransactions.stream()
                .collect(toMap(Function.identity(), symbol -> calculateForUserAndSymbol(userId, symbol)));
    }

    public Double calculateTotalInvestmentValue(Long userId) {
        Map<String, Double> investmentValues = calculateInvestmentValuePerStock(userId);
        return investmentValues.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    private Double calculateForUserAndSymbol(Long userId, String stockSymbol) {
        List<StockTransaction> transactionsForStock = stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol);

        List<StockTransaction> remainingBuys = new ArrayList<>();

        for (StockTransaction tx : transactionsForStock) {
            if (tx.getType().equals(BUY)) {
                remainingBuys.add(tx);
            } else if (tx.getType().equals(SELL)) {
                matchSellWithBuyTransactions(tx, remainingBuys);
            } else {
                throw new IllegalArgumentException("Unknown transaction type: " + tx.getType());
            }
        }

        return calculateInvestmentValue(stockSymbol, remainingBuys);
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
            } else {
                buyTx.setQuantity(buyQty - quantityToSell);
                quantityToSell = 0;
            }
        }
    }

    private Double calculateInvestmentValue(String stockSymbol, List<StockTransaction> transactions) {
        return transactions.stream()
                .mapToDouble(tx -> (tx.getQuantity() * tx.getPrice()) + tx.getFee())
                .sum();
    }
} 