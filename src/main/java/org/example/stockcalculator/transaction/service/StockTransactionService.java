package org.example.stockcalculator.transaction.service;

import static org.example.stockcalculator.account.utils.AuthUtils.currentUserId;

import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.transaction.dto.TransactionPayload;
import org.example.stockcalculator.transaction.mapper.StockTransactionMapper;
import org.example.stockcalculator.transaction.repository.StockTransactionRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;
    private final StockTransactionMapper stockTransactionMapper;

    public void saveStockTransaction(TransactionPayload stockTransaction) {
        StockTransaction stockTransactionEntity = stockTransactionMapper.toEntity(stockTransaction);
        stockTransactionEntity.setUser(new UserAccount(currentUserId()));
        stockTransactionRepository.save(stockTransactionEntity);
    }

    public void updateStockTransaction(Long transactionId, TransactionPayload stockTransaction) {
        StockTransaction stockTransactionEntity = stockTransactionMapper.toEntity(stockTransaction);
        stockTransactionEntity.setUser(new UserAccount(currentUserId()));
        stockTransactionEntity.setId(transactionId);

        stockTransactionRepository.save(stockTransactionEntity);
    }

    public void deleteStockTransaction(Long transactionId) {
        stockTransactionRepository.deleteById(transactionId);
    }

}
