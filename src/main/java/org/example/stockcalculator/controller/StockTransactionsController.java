package org.example.stockcalculator.controller;

import org.example.stockcalculator.dto.CreateTransactionRequest;
import org.example.stockcalculator.service.StockTransactionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock-transactions")
@RequiredArgsConstructor
public class StockTransactionsController {

    private final StockTransactionService stockTransactionService;

    @PostMapping
    public void createTransaction(@RequestBody @Valid CreateTransactionRequest request) {
        stockTransactionService.saveStockTransaction(request);
    }

}
