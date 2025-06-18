package org.example.stockcalculator.integration;

import static org.example.stockcalculator.entity.TransactionType.BUY;
import static org.example.stockcalculator.entity.TransactionType.SELL;

import java.util.List;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.integration.trading212.Trading212StockTransactionsClient;
import org.example.stockcalculator.repository.StockTransactionRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockTransactionManager {

    private final Trading212StockTransactionsClient stockTransactionsClient;
    private final StockTransactionRepository stockTransactionRepository;

    public void syncTransactionsToDb() {
        List<StockTransaction> stockTransactions = stockTransactionsClient.fetchTransactionsOfUser()
                .stream()
                .map(StockTransactionManager::convertToStockTransactionEntity)
                .toList();
        stockTransactionRepository.saveAll(stockTransactions);
    }

    private static StockTransaction convertToStockTransactionEntity(Trading212StockTransactionsClient.Transaction tx) {
        StockTransaction stockTransaction = new StockTransaction();
        stockTransaction.setStock(new Stock(1L));
        stockTransaction.setUser(new UserAccount(1L));
        stockTransaction.setPrice(tx.fillPrice());
        stockTransaction.setQuantity(tx.orderedValue() / tx.fillPrice());
        stockTransaction.setTimeOfTransaction(tx.dateModified());
        stockTransaction.setFee(0.0); // todo add fee
        stockTransaction.setType(tx.orderedValue() > 0 ? BUY : SELL);
        return stockTransaction;
    }
}
