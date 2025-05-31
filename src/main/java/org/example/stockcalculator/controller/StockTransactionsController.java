package org.example.stockcalculator.controller;

import java.util.List;

import org.example.stockcalculator.dto.CreateTransactionRequest;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.integration.StockTransactionManager;
import org.example.stockcalculator.repository.StockTransactionRepository;
import org.example.stockcalculator.service.StockTransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock/transactions")
@RequiredArgsConstructor
public class StockTransactionsController {

    private final StockTransactionService stockTransactionService;
    private final StockTransactionRepository stockTransactionRepository;
    private final StockTransactionManager manager;

    @PostMapping
    public void createTransaction(@RequestBody @Valid CreateTransactionRequest request) {
        stockTransactionService.saveStockTransaction(request);
    }

    @GetMapping
    public List<StockTransaction> getAllTransactions(@Valid @NotNull @RequestParam Long userId) {

        return stockTransactionRepository.findByUserIdOrderByTimeOfTransactionAsc(userId);
    }


    @PostMapping("sync")
    public void syncTransactions(){
        manager.syncTransactionsToDb();
    }
}
