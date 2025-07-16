package org.example.stockcalculator.stock;

import org.example.stockcalculator.stock.profit.StockProfit;
import org.example.stockcalculator.stock.profit.StockProfitService;
import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.price.repository.StockPriceRepository;
import org.example.stockcalculator.transaction.repository.StockTransactionRepository;
import org.example.stockcalculator.transaction.service.UnsoldStockTransactionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class StockProfitServiceTest {
    private StockTransactionRepository stockTransactionRepository;
    private StockPriceRepository stockPriceRepository;
    private UnsoldStockTransactionsService unsoldStockTransactionsService;
    private StockProfitService stockProfitService;

    @BeforeEach
    void setUp() {
        stockTransactionRepository = Mockito.mock(StockTransactionRepository.class);
        stockPriceRepository = Mockito.mock(StockPriceRepository.class);
        unsoldStockTransactionsService = Mockito.mock(UnsoldStockTransactionsService.class);
        stockProfitService = new StockProfitService(stockTransactionRepository, stockPriceRepository, unsoldStockTransactionsService);
    }

    @Test
    void testSingleStockSingleTransaction() {
        Long userId = 1L;
        String symbol = "AAPL";
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        when(stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(userId)).thenReturn(List.of(stock));

        StockTransaction tx = new StockTransaction();
        tx.setPrice(100.0);
        tx.setQuantity(2.0);
        tx.setFee(1.0);
        when(unsoldStockTransactionsService.getUnsoldStockTransactions(userId, symbol)).thenReturn(List.of(tx));

        StockPriceEntity price = new StockPriceEntity();
        price.setPrice(120.0);
        when(stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(symbol)).thenReturn(price);

        Map<String, StockProfit> result = stockProfitService.calculateProfitForSymbolWithInvestmentInfo(userId);
        StockProfit profit = result.get(symbol);
        assertNotNull(profit);
        assertEquals(39.0, profit.profit(), 0.0001); // (120-100)*2 - 1 = 39
        assertEquals(19.5, profit.profitPercentage(), 0.0001); // 39/200*100
        assertEquals(240.0, profit.currentValue(), 0.0001); // 120*2
        assertEquals(201.0, profit.investedAmountInUsd(), 0.0001); // 100*2+1
        assertEquals(2.0, profit.totalShares(), 0.0001);
    }

    @Test
    void testNoTransactions() {
        Long userId = 1L;
        String symbol = "AAPL";
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        when(stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(userId)).thenReturn(List.of(stock));
        when(unsoldStockTransactionsService.getUnsoldStockTransactions(userId, symbol)).thenReturn(Collections.emptyList());
        when(stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(symbol)).thenReturn(null);

        Map<String, StockProfit> result = stockProfitService.calculateProfitForSymbolWithInvestmentInfo(userId);
        StockProfit profit = result.get(symbol);
        assertNotNull(profit);
        assertEquals(0.0, profit.profit(), 0.0001);
        assertEquals(0.0, profit.profitPercentage(), 0.0001);
        assertEquals(0.0, profit.currentValue(), 0.0001);
        assertEquals(0.0, profit.investedAmountInUsd(), 0.0001);
        assertEquals(0.0, profit.totalShares(), 0.0001);
    }

    @Test
    void testMultipleTransactions() {
        Long userId = 1L;
        String symbol = "AAPL";
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        when(stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(userId)).thenReturn(List.of(stock));

        StockTransaction tx1 = new StockTransaction();
        tx1.setPrice(100.0);
        tx1.setQuantity(1.0);
        tx1.setFee(1.0);
        StockTransaction tx2 = new StockTransaction();
        tx2.setPrice(110.0);
        tx2.setQuantity(2.0);
        tx2.setFee(2.0);
        when(unsoldStockTransactionsService.getUnsoldStockTransactions(userId, symbol)).thenReturn(List.of(tx1, tx2));

        StockPriceEntity price = new StockPriceEntity();
        price.setPrice(120.0);
        when(stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(symbol)).thenReturn(price);

        Map<String, StockProfit> result = stockProfitService.calculateProfitForSymbolWithInvestmentInfo(userId);
        StockProfit profit = result.get(symbol);
        assertNotNull(profit);
        // profit: (120-100)*1-1 + (120-110)*2-2 = 19 + 8 + 8 = 33
        assertEquals(33.0, profit.profit(), 0.0001);
        // invested: 100*1+1 + 110*2+2 = 101 + 222 = 323
        assertEquals(323.0, profit.investedAmountInUsd(), 0.0001);
        // shares: 1+2=3
        assertEquals(3.0, profit.totalShares(), 0.0001);
        // current value: 120*3=360
        assertEquals(360.0, profit.currentValue(), 0.0001);
        // profit percent: 33/(100*1+110*2)*100 = 33/320*100 = 10.3125
        assertEquals(10.3125, profit.profitPercentage(), 0.0001);
    }
} 
