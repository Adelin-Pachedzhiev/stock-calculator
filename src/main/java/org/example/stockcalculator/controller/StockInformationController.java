package org.example.stockcalculator.controller;

import java.util.List;
import java.util.Optional;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.model.StockPrice;
import org.example.stockcalculator.repository.StockRepository;
import org.example.stockcalculator.service.StockPriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockInformationController {

    public final StockRepository stockRepository;
    public final StockPriceService stockPriceService;

    @GetMapping("/currentPrice/{stockSymbol}")
    public Optional<StockPrice> getCurrentPrice(@PathVariable String stockSymbol) {
        return stockPriceService.getCurrentPrice(stockSymbol);
    }

    @GetMapping("/availableStocks")
    public List<Stock> getAvailableStocks() {
        return stockRepository.findAll();
    }

}
