package org.example.stockcalculator.portfolio.service;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.transaction.repository.StockTransactionRepository;
import org.example.stockcalculator.transaction.service.UnsoldStockTransactionsService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockInvestmentValueService {

    private final StockTransactionRepository stockTransactionRepository;
    private final UnsoldStockTransactionsService unsoldStockTransactionsService;

    public Double calculateTotalInvestmentValue(Long userId) {
        Map<String, Double> investmentValues = calculateInvestmentValuePerStock(userId);
        return investmentValues.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public Map<String, Double> calculateInvestmentValuePerStock(Long userId) {
        List<Stock> stockSymbolsOfTransactions = stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(userId);
        return stockSymbolsOfTransactions.stream()
                .collect(toMap(Stock::getSymbol, stock -> calculateInvestmentValue(userId, stock.getSymbol())));
    }

    private Double calculateInvestmentValue(Long userId, String stockSymbol) {
        List<StockTransaction> remainingBuys = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        return remainingBuys.stream()
                .mapToDouble(tx -> (tx.getQuantity() * tx.getPrice()) + tx.getFee())
                .sum();
    }
} 
