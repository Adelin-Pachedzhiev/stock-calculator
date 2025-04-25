package org.example.stockcalculator.repository;

import org.example.stockcalculator.entity.StockPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPriceEntity, Long> {
}
