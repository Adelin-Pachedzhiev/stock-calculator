package org.example.stockcalculator.transaction.service;

import static org.example.stockcalculator.entity.TransactionType.BUY;
import static org.example.stockcalculator.entity.TransactionType.SELL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.transaction.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnsoldStockTransactionsService {

    private final StockTransactionRepository stockTransactionRepository;

    public List<StockTransaction> getUnsoldStockTransactions(Long userId, String stockSymbol) {
        List<StockTransaction> transactionsForStock = stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol);

        List<StockTransaction> remainingBuys = new ArrayList<>();

        for (StockTransaction tx : transactionsForStock) {
            if (tx.getType().equals(BUY)) {
                remainingBuys.add(tx);
            }
            else if (tx.getType().equals(SELL)) {
                matchSellWithBuyTransactions(tx, remainingBuys);
            }
            else {
                throw new IllegalArgumentException("Unknown transaction type: " + tx.getType());
            }
        }
        return remainingBuys;
    }

    private void matchSellWithBuyTransactions(StockTransaction tx, List<StockTransaction> remainingBuys) {
        double quantityToSell = tx.getQuantity();

        Iterator<StockTransaction> iterator = remainingBuys.iterator();
        while (iterator.hasNext() && quantityToSell > 0) {
            StockTransaction buyTx = iterator.next();
            double buyQty = buyTx.getQuantity();

            if (buyQty <= quantityToSell) {
                quantityToSell -= buyQty;
                iterator.remove();
            }
            else {
                buyTx.setQuantity(buyQty - quantityToSell);
                quantityToSell = 0;
            }
        }
    }
}
