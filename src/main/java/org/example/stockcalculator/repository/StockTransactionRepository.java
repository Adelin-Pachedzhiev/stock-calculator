package org.example.stockcalculator.repository;

import org.example.stockcalculator.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

}
