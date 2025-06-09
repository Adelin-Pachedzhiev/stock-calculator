package org.example.stockcalculator.controller;

import static org.example.stockcalculator.auth.utils.AuthUtils.currentUserId;

import java.util.List;

import org.example.stockcalculator.model.StockProfit;
import org.example.stockcalculator.service.StockProfitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock/profit")
@RequiredArgsConstructor
public class StockProfitController {

    private final StockProfitService stockProfitService;

    @GetMapping
    public List<StockProfitForSymbol> calculate() {
        Long userId = currentUserId();
        return stockProfitService.calculateProfitPerStock(userId).entrySet().stream()
                .map(profit-> new StockProfitForSymbol(profit.getKey(), profit.getValue()))
                .toList();
    }

    @GetMapping("/total")
    public StockProfit calculateTotalProfit() {
        Long userId = currentUserId();
        return stockProfitService.calculateTotalProfit(userId);
    }

    public record StockProfitForSymbol(String symbol, StockProfit stockProfit) {
    }
}
