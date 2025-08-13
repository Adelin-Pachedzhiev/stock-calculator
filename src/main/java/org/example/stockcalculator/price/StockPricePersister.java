package org.example.stockcalculator.price;

import static org.example.stockcalculator.util.SleepUtil.sleepSilentlyForSeconds;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.price.client.StockPriceApiClient;
import org.example.stockcalculator.price.mapper.StockPriceMapper;
import org.example.stockcalculator.portfolio.dto.StockPriceResponse;
import org.example.stockcalculator.price.repository.StockPriceRepository;
import org.example.stockcalculator.stock.repository.StockRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "stock.prices-api.enabled", havingValue = "true", matchIfMissing = true)
public class StockPricePersister {

    private static final int DELAY_SECONDS_AFTER_REQUEST = 2;
    private final StockPriceApiClient stockPriceApiClient;
    private final StockPriceRepository stockPriceRepository;
    private final StockRepository stockRepository;
    private final ExecutorService executorService;
    private final StockPriceMapper stockPriceMapper;

    @PostConstruct
    public void startPersistingStockPrices() {
        log.info("Stock price persistence enabled. Starting to persist stock prices...");
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
        supportedStocks.forEach((stock -> {
            sleepSilentlyForSeconds(DELAY_SECONDS_AFTER_REQUEST);
            getPriceAndSaveToDb(stock);
        }));
    }

    private void getPriceAndSaveToDb(Stock stock) {
        stockPriceApiClient.getPriceForSymbol(stock.getSymbol())
                .map(price -> createStockPriceEntity(stock, price))
                .ifPresent(stockPriceRepository::save);
    }

    private StockPriceEntity createStockPriceEntity(Stock stock, StockPriceResponse priceResponse) {
        StockPriceEntity priceEntity = stockPriceMapper.toEntity(priceResponse);
        priceEntity.setStock(stock);
        priceEntity.setTimestamp(LocalDateTime.now());

        return priceEntity;
    }
}
