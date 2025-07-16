package org.example.stockcalculator.transaction.controller;

import static org.example.stockcalculator.account.utils.AuthUtils.currentUserId;

import java.util.List;
import java.util.Optional;

import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.integration.StockTransactionManager;
import org.example.stockcalculator.transaction.repository.StockTransactionRepository;
import org.example.stockcalculator.transaction.dto.TransactionPayload;
import org.example.stockcalculator.transaction.service.StockTransactionService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock/transactions")
@RequiredArgsConstructor
public class StockTransactionsController {

    private final StockTransactionService stockTransactionService;
    private final StockTransactionRepository stockTransactionRepository;
    private final StockTransactionManager manager;

    @GetMapping
    public List<StockTransaction> getAllTransactions(@RequestParam(required = false) String symbol) {
        Long userId = currentUserId();

        if (symbol == null) {
            return stockTransactionRepository.findByUserIdOrderByTimeOfTransactionAsc(userId);
        }
        return stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, symbol.toUpperCase());
    }

    @GetMapping("/{transactionId}")
    public Optional<StockTransaction> getAllTransactionsForSymbol(@PathVariable Long transactionId) {
        return stockTransactionRepository.findById(transactionId);
    }

    @PostMapping
    public void createTransaction(@RequestBody @Valid TransactionPayload request) {
        stockTransactionService.saveStockTransaction(request);
    }

    @PutMapping("/{transactionId}")
    public void updateTransaction(@RequestBody @Valid TransactionPayload request, @PathVariable Long transactionId) {
        stockTransactionService.updateStockTransaction(transactionId, request);
    }

    @DeleteMapping("/{transactionId}")
    public void deleteTransaction(@PathVariable Long transactionId) {
        stockTransactionService.deleteStockTransaction(transactionId);
    }
}
