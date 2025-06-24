package org.example.stockcalculator.transaction;

import static org.example.stockcalculator.auth.utils.AuthUtils.currentUserId;

import java.util.List;

import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.integration.StockTransactionManager;
import org.example.stockcalculator.repository.StockTransactionRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping
    public void createTransaction(@RequestBody @Valid TransactionPayload request) {
        stockTransactionService.saveStockTransaction(request);
    }

    @GetMapping
    public List<StockTransaction> getAllTransactions() {
        Long userId = currentUserId();

        return stockTransactionRepository.findByUserIdOrderByTimeOfTransactionAsc(userId);
    }

    @PutMapping("/{transactionId}")
    public void updateTransaction(@RequestBody @Valid TransactionPayload request, @PathVariable Long transactionId) {
        stockTransactionService.updateStockTransaction(transactionId, request);
    }

    @DeleteMapping("/{transactionId}")
    public void deleteTransaction(@PathVariable Long transactionId) {
        stockTransactionService.deleteStockTransaction(transactionId);
    }



    @PostMapping("sync")
    public void syncTransactions(){
        manager.syncTransactionsToDb();
    }
}
