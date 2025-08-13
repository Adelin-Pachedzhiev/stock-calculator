package org.example.stockcalculator.stock.profit.controller;

import static org.example.stockcalculator.account.utils.AuthUtils.currentUserId;

import java.util.List;

import org.example.stockcalculator.stock.profit.dto.StockInvestmentProfitInfo;
import org.example.stockcalculator.stock.profit.dto.StockProfit;
import org.example.stockcalculator.stock.profit.service.StockProfitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock/profit")
@RequiredArgsConstructor
public class StockProfitController {

    private final StockProfitService stockProfitService;

    @GetMapping
    public List<StockInvestmentProfitInfo> calculate() {
        return stockProfitService.calculateProfitInfoBySymbol();
    }

    @GetMapping("/total")
    public StockProfit calculateTotalProfit() {
        Long userId = currentUserId();
        return stockProfitService.calculateTotalProfit(userId);
    }

    @GetMapping("/{stockSymbol}")
    public StockProfit calculateProfitBySymbol(@PathVariable String stockSymbol) {
        return stockProfitService.calculateProfitForSymbol(stockSymbol);
    }
}
