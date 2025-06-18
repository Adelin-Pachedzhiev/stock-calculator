package org.example.stockcalculator.service;

import static org.example.stockcalculator.auth.utils.AuthUtils.currentUserId;

import org.example.stockcalculator.dto.CreateTransactionRequest;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.mapper.StockTransactionMapper;
import org.example.stockcalculator.repository.StockTransactionRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;
    private final StockTransactionMapper stockTransactionMapper;

    public void saveStockTransaction(CreateTransactionRequest stockTransaction) {
        StockTransaction stockTransactionEntity = stockTransactionMapper.toEntity(stockTransaction);
        stockTransactionEntity.setUser(new UserAccount(currentUserId()));
        stockTransactionRepository.save(stockTransactionEntity);
    }

}
