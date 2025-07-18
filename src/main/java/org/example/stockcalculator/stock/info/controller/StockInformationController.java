package org.example.stockcalculator.stock.info.controller;

import java.util.List;
import java.util.Optional;

import org.example.stockcalculator.stock.info.service.StockInformationService;
import org.example.stockcalculator.stock.info.dto.StockInformationResponse;
import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.price.dto.StockPrice;
import org.example.stockcalculator.stock.repository.StockRepository;
import org.example.stockcalculator.price.service.StockPriceService;
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
    public final StockInformationService stockInformationService;

    @GetMapping("/currentPrice/{stockSymbol}")
    public Optional<StockPrice> getCurrentPrice(@PathVariable String stockSymbol) {
        return stockPriceService.getCurrentPrice(stockSymbol);
    }

    @GetMapping
    public List<Stock> getAvailableStocks() {
        return stockRepository.findAll();
    }

    @GetMapping("/information/{stockSymbol}")
    public StockInformationResponse getStockInformation(@PathVariable String stockSymbol) {
        return stockInformationService.getStockInformation(stockSymbol);
    }

}
