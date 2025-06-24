package org.example.stockcalculator.service;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockInvestmentValueService {

    private final StockTransactionRepository stockTransactionRepository;
    private final UnsoldStockTransactionsService unsoldStockTransactionsService;

    public Map<String, Double> calculateInvestmentValuePerStock(Long userId) {
        List<Stock> stockSymbolsOfTransactions = stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(userId);
        return stockSymbolsOfTransactions.stream()
                .collect(toMap(Stock::getSymbol, stock -> calculateInvestmentValue(userId, stock.getSymbol())));
    }

    public Double calculateTotalInvestmentValue(Long userId) {
        Map<String, Double> investmentValues = calculateInvestmentValuePerStock(userId);
        return investmentValues.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    private Double calculateInvestmentValue(Long userId, String stockSymbol) {
        List<StockTransaction> remainingBuys = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        return remainingBuys.stream()
                .mapToDouble(tx -> (tx.getQuantity() * tx.getPrice()) + tx.getFee())
                .sum();
    }
} 
