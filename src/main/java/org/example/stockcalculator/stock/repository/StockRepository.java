package org.example.stockcalculator.stock.repository;

import java.util.Optional;
import java.util.Set;

import org.example.stockcalculator.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findBySymbol(String symbol);

    @Query("SELECT s.symbol FROM Stock s")
    Set<String> findAllSymbols();
}
