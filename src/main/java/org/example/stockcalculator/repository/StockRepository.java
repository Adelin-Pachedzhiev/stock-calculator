package org.example.stockcalculator.repository;

import org.example.stockcalculator.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
