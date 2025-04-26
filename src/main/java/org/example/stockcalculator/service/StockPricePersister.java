package org.example.stockcalculator.service;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.LocalDateTime;
import java.util.List;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.model.StockPrice;
import org.example.stockcalculator.repository.StockPriceRepository;
import org.example.stockcalculator.repository.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockPricePersister {

    private final StockPriceApiClient stockPriceApiClient;
    private final StockPriceRepository stockPriceRepository;
    private final StockRepository stockRepository;

    @Scheduled(fixedRate = 8, timeUnit = SECONDS)
    public void persistStockPrice() {
        List<Stock> supportedStocks = stockRepository.findAll();
//        log.info("Supported stocks: {}", supportedStocks.stream().map(Stock::getSymbol).toList());
        supportedStocks.forEach(this::getPriceAndSaveToDb);
    }

    private void getPriceAndSaveToDb(Stock stock) {
        stockPriceApiClient.getPriceForSymbol(stock.getSymbol())
                .map(price -> createStockPriceEntity(stock, price))
                .ifPresent(priceEntity -> {
                    stockPriceRepository.save(priceEntity);
//                    log.info("Saved stock price {} for stock {}", priceEntity.getPrice(), stock.getName());
                });
    }

    private StockPriceEntity createStockPriceEntity(Stock stock, StockPrice price) {
        StockPriceEntity stockPriceEntity = new StockPriceEntity();
        stockPriceEntity.setStock(stock);
        stockPriceEntity.setPrice(price.currentPrice());
        stockPriceEntity.setTimestamp(LocalDateTime.now());
        return stockPriceEntity;
    }
}
