package org.example.stockcalculator.controller;

import java.util.Map;

import org.example.stockcalculator.model.StockProfit;
import org.example.stockcalculator.service.StockProfitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock-profit")
@RequiredArgsConstructor
public class StockProfitController {
    private final StockProfitService stockProfitService;

    @GetMapping("/")
    public Map<String, StockProfit> calculate(@Valid @NotNull @RequestParam Long userId) {
        return stockProfitService.calculate(userId);
    }
}
