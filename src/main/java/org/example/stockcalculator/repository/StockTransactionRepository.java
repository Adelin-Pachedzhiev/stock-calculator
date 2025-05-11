package org.example.stockcalculator.repository;

import java.util.List;

import org.example.stockcalculator.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findByUserIdAndStockSymbolOrderByTimestampAsc(Long user_id, String stockSymbol);

    @Query("SELECT DISTINCT t.stock.symbol FROM StockTransaction t WHERE t.user.id = :userId")
    List<String> findStockSymbolsOfTransactionsByUserId(@Param("userId") Long userId);

}
