package org.example.stockcalculator.stock.info.service;

import java.util.List;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.price.repository.StockPriceRepository;
import org.example.stockcalculator.stock.info.dto.StockInformationResponse;
import org.example.stockcalculator.stock.repository.StockRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockInformationService {

    private final StockPriceRepository stockPriceRepository;
    private final StockRepository stockRepository;

    public StockInformationResponse getStockInformation(String symbol) {
        StockPriceEntity price = stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(symbol.toUpperCase());
        Stock stock = price.getStock();
        return new StockInformationResponse(stock.getId(),
                                            stock.getSymbol(),
                                            stock.getName(),
                                            stock.getDescription(),
                                            price.getPrice(),
                                            price.getChange(),
                                            price.getChangePercent());
    }

    public List<Stock> getAvailableStocks() {
        return stockRepository.findAll()
                .stream()
                .filter(this::hasAnyPriceRecords)
                .toList();
    }

    private boolean hasAnyPriceRecords(Stock stock) {
        return stockPriceRepository.countByStockSymbol(stock.getSymbol()) > 0;
    }
}
