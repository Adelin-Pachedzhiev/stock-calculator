package org.example.stockcalculator.repository;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.example.stockcalculator.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

     List<StockTransaction> findByUserId(Long user_id);

     default Map<String, List<StockTransaction>> findByUserIdGroupedByStockSymbol(Long user_id){
            List<StockTransaction> transactions = findByUserId(user_id);
            return transactions.stream()
                    .collect(groupingBy(tr->tr.getStock().getSymbol()));
     };
}
