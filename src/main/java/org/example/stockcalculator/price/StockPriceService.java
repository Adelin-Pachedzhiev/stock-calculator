package org.example.stockcalculator.price;

import java.util.Optional;

import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.price.dto.StockPrice;
import org.example.stockcalculator.price.repository.StockPriceRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockPriceService {

    private final StockPriceRepository stockPriceRepository;

    public Optional<StockPrice> getCurrentPrice(String stockSymbol) {
        StockPriceEntity currentPriceEntity = stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(stockSymbol);
        if (currentPriceEntity == null) {
            return Optional.empty();
        }
        StockPrice stockPrice = new StockPrice(stockSymbol, currentPriceEntity.getPrice());

        return Optional.of(stockPrice);
    }

}
