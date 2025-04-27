package org.example.stockcalculator.controller;

import org.example.stockcalculator.repository.StockPriceRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockInformationController {

    public final StockPriceRepository stockPriceRepository;

    @GetMapping("/currentPrice/{stockSymbol}")
    public double getCurrentPrice(@PathVariable String stockSymbol){
        // todo create dto
        return stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(stockSymbol).getPrice();
    }
}
