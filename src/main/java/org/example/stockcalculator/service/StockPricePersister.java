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

    @Scheduled(fixedRate = 6, timeUnit = SECONDS)
    public void persistStockPrice() {
        List<Stock> supportedStocks = stockRepository.findAll();
        log.info("Supported stocks: {}", supportedStocks.stream().map(Stock::getName).toList());
        supportedStocks.forEach(stock -> {
            StockPrice stockPriceFromApi = stockPriceApiClient.getPriceForSymbol(stock.getSymbol()).orElseThrow();

            StockPriceEntity stockPriceEntity = new StockPriceEntity();
            stockPriceEntity.setStock(stock);
            stockPriceEntity.setPrice(stockPriceFromApi.currentPrice());
            stockPriceEntity.setTimestamp(LocalDateTime.now());


            stockPriceRepository.save(stockPriceEntity);
        });
    }
}
