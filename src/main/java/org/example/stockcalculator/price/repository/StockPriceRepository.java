package org.example.stockcalculator.price.repository;

import org.example.stockcalculator.entity.StockPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPriceEntity, Long> {

    StockPriceEntity findTopByStockSymbolOrderByTimestampDesc(String stockSymbol);

    int countByStockSymbol(String stockSymbol);

    void deleteByStockId(Long stockId);
}
