package org.example.stockcalculator.stock.profit;

import static org.example.stockcalculator.auth.utils.AuthUtils.currentUserId;

import java.util.List;
import java.util.Map;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.stock.repository.StockRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock/profit")
@RequiredArgsConstructor
public class StockProfitController {

    private final StockProfitService stockProfitService;
    private final StockRepository stockRepository; 

    @GetMapping
    public List<StockInvestmentProfitInfo> calculate() {
        Long userId = currentUserId();
        return stockProfitService.calculateProfitForSymbolWithInvestmentInfo(userId).entrySet().stream()
            .map(this::convertToProfitInfo)
            .toList();
    }

    @NotNull
    private StockInvestmentProfitInfo convertToProfitInfo(Map.Entry<String, StockProfit> entry) {
        Stock stock = stockRepository.findBySymbol(entry.getKey()).orElseThrow();
        return new StockInvestmentProfitInfo(stock, entry.getValue());
    }

    @GetMapping("/total")
    public StockProfit calculateTotalProfit() {
        Long userId = currentUserId();
        return stockProfitService.calculateTotalProfit(userId);
    }
}
