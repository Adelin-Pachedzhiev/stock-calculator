package org.example.stockcalculator.transaction.repository;

import java.util.List;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(Long user_id, String stockSymbol);

    List<StockTransaction> findByUserIdOrderByTimeOfTransactionAsc(Long user_id);


    @Query("SELECT DISTINCT t.stock FROM StockTransaction t WHERE t.user.id = :userId")
    List<Stock> findStockSymbolsOfTransactionsByUserId(@Param("userId") Long userId);


    void deleteByPlatformIntegrationId(Long platformIntegrationId);
}
