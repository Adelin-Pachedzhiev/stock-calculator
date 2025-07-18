package org.example.stockcalculator.stock.info.service;

import org.example.stockcalculator.stock.info.dto.StockInformationResponse;
import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.price.repository.StockPriceRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockInformationService {

    private final StockPriceRepository stockPriceRepository;

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
}
