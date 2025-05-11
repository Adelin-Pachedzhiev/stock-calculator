package org.example.stockcalculator.service;

import static org.example.stockcalculator.util.SleepUtil.sleepSilentlyForSeconds;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.model.StockPriceResponse;
import org.example.stockcalculator.repository.StockPriceRepository;
import org.example.stockcalculator.repository.StockRepository;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockPricePersister {

    private static final int API_REQUESTS_PER_MINUTE_LIMIT = 60;
    private final StockPriceApiClient stockPriceApiClient;
    private final StockPriceRepository stockPriceRepository;
    private final StockRepository stockRepository;
    private final ExecutorService executorService;

    @PostConstruct
    public void startPersistingStockPrices() {
        executorService.submit(this::persistStockPricesForever);
    }

    private void persistStockPricesForever() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                getStocksAndPersistTheirPrices();
            }
            catch (Exception e) {
                log.error("Error occurred while persisting stock prices: {}", e.getMessage(), e);
            }
        }
    }

    private void getStocksAndPersistTheirPrices() {
        List<Stock> supportedStocks = stockRepository.findAll();
        int delayBetweenRequests = calculateDelayBetweenRequests(supportedStocks);
        supportedStocks.forEach((stock -> {
            sleepSilentlyForSeconds(delayBetweenRequests);
            getPriceAndSaveToDb(stock);
        }));
    }

    private int calculateDelayBetweenRequests(List<Stock> supportedStocks) {
        int stocksCount = supportedStocks.size();
        int requestsForAStockPerMinute = API_REQUESTS_PER_MINUTE_LIMIT / stocksCount;
        return 60 / requestsForAStockPerMinute;
    }

    private void getPriceAndSaveToDb(Stock stock) {
        stockPriceApiClient.getPriceForSymbol(stock.getSymbol())
                .map(price -> createStockPriceEntity(stock, price))
                .ifPresent(stockPriceRepository::save);
    }

    private StockPriceEntity createStockPriceEntity(Stock stock, StockPriceResponse price) {
        StockPriceEntity stockPriceEntity = new StockPriceEntity();
        stockPriceEntity.setStock(stock);
        stockPriceEntity.setPrice(price.currentPrice());
        stockPriceEntity.setTimestamp(LocalDateTime.now());
        return stockPriceEntity;
    }
}
