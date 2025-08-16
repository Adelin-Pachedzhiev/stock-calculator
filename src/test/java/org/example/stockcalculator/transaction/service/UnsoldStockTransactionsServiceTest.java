package org.example.stockcalculator.transaction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.stockcalculator.entity.TransactionType.BUY;
import static org.example.stockcalculator.entity.TransactionType.SELL;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.entity.TransactionType;
import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.transaction.repository.StockTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnsoldStockTransactionsServiceTest {

    @Mock
    private StockTransactionRepository stockTransactionRepository;

    @InjectMocks
    private UnsoldStockTransactionsService unsoldStockTransactionsService;

    private UserAccount user;
    private Stock stock;
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        user = new UserAccount();
        user.setId(1L);

        stock = new Stock();
        stock.setSymbol("AAPL");

        baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
    }

    @Test
    void getUnsoldStockTransactions_emptyTransactionList_returnsEmptyList() {
        Long userId = 1L;
        String stockSymbol = "AAPL";

        when(stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol))
                .thenReturn(Collections.emptyList());

        List<StockTransaction> result = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        assertThat(result).isEmpty();
    }

    @Test
    void getUnsoldStockTransactions_onlyBuyTransactions_returnsAllBuys() {
        Long userId = 1L;
        String stockSymbol = "AAPL";

        StockTransaction buy1 = createTransaction(BUY, 10.0, baseTime);
        StockTransaction buy2 = createTransaction(BUY, 20.0, baseTime.plusMinutes(10));

        when(stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol))
                .thenReturn(Arrays.asList(buy1, buy2));

        List<StockTransaction> result = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        assertThat(result)
                .hasSize(2)
                .containsExactly(buy1, buy2);
    }

    @Test
    void getUnsoldStockTransactions_buySellExactMatch_returnsEmpty() {
        Long userId = 1L;
        String stockSymbol = "AAPL";

        StockTransaction buy = createTransaction(BUY, 10.0, baseTime);
        StockTransaction sell = createTransaction(SELL, 10.0, baseTime.plusMinutes(10));

        when(stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol))
                .thenReturn(Arrays.asList(buy, sell));

        List<StockTransaction> result = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        assertThat(result).isEmpty();
    }

    @Test
    void getUnsoldStockTransactions_buyMoreThanSold_returnsPartialBuy() {
        Long userId = 1L;
        String stockSymbol = "AAPL";

        StockTransaction buy = createTransaction(BUY, 15.0, baseTime);
        StockTransaction sell = createTransaction(SELL, 10.0, baseTime.plusMinutes(10));

        when(stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol))
                .thenReturn(Arrays.asList(buy, sell));

        List<StockTransaction> result = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuantity()).isEqualTo(5.0);
        assertThat(result.get(0).getType()).isEqualTo(BUY);
    }

    @Test
    void getUnsoldStockTransactions_sellMoreThanBought_consumesAllBuys() {
        Long userId = 1L;
        String stockSymbol = "AAPL";

        StockTransaction buy = createTransaction(BUY, 10.0, baseTime);
        StockTransaction sell = createTransaction(SELL, 15.0, baseTime.plusMinutes(10));

        when(stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol))
                .thenReturn(Arrays.asList(buy, sell));

        List<StockTransaction> result = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        assertThat(result).isEmpty();
    }

    @Test
    void getUnsoldStockTransactions_multipleBuysOneSell_sellConsumesMultipleBuys() {
        Long userId = 1L;
        String stockSymbol = "AAPL";

        StockTransaction buy1 = createTransaction(BUY, 10.0, baseTime);
        StockTransaction buy2 = createTransaction(BUY, 5.0, baseTime.plusMinutes(5));
        StockTransaction sell = createTransaction(SELL, 12.0, baseTime.plusMinutes(10));

        when(stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol))
                .thenReturn(Arrays.asList(buy1, buy2, sell));

        List<StockTransaction> result = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuantity()).isEqualTo(3.0);
        assertThat(result.get(0).getType()).isEqualTo(BUY);
    }

    @Test
    void getUnsoldStockTransactions_complexScenario_correctlyCalculatesRemaining() {
        Long userId = 1L;
        String stockSymbol = "AAPL";

        StockTransaction buy1 = createTransaction(BUY, 100.0, baseTime);
        StockTransaction sell1 = createTransaction(SELL, 30.0, baseTime.plusMinutes(10));
        StockTransaction buy2 = createTransaction(BUY, 50.0, baseTime.plusMinutes(20));
        StockTransaction sell2 = createTransaction(SELL, 80.0, baseTime.plusMinutes(30));
        StockTransaction buy3 = createTransaction(BUY, 25.0, baseTime.plusMinutes(40));

        when(stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol))
                .thenReturn(Arrays.asList(buy1, sell1, buy2, sell2, buy3));

        List<StockTransaction> result = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getQuantity()).isEqualTo(40.0);
        assertThat(result.get(1).getQuantity()).isEqualTo(25.0);
    }

    @Test
    void getUnsoldStockTransactions_onlySellTransactions_returnsEmpty() {
        Long userId = 1L;
        String stockSymbol = "AAPL";

        StockTransaction sell1 = createTransaction(SELL, 10.0, baseTime);
        StockTransaction sell2 = createTransaction(SELL, 20.0, baseTime.plusMinutes(10));

        when(stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol))
                .thenReturn(Arrays.asList(sell1, sell2));

        List<StockTransaction> result = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        assertThat(result).isEmpty();
    }

    @Test
    void getUnsoldStockTransactions_alternatingBuySell_correctlyProcesses() {
        Long userId = 1L;
        String stockSymbol = "AAPL";

        StockTransaction buy1 = createTransaction(BUY, 20.0, baseTime);
        StockTransaction sell1 = createTransaction(SELL, 5.0, baseTime.plusMinutes(5));
        StockTransaction buy2 = createTransaction(BUY, 10.0, baseTime.plusMinutes(10));
        StockTransaction sell2 = createTransaction(SELL, 15.0, baseTime.plusMinutes(15));
        StockTransaction buy3 = createTransaction(BUY, 8.0, baseTime.plusMinutes(20));

        when(stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol))
                .thenReturn(Arrays.asList(buy1, sell1, buy2, sell2, buy3));

        List<StockTransaction> result = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getQuantity()).isEqualTo(10.0);
        assertThat(result.get(1).getQuantity()).isEqualTo(8.0);
    }

    @Test
    void getUnsoldStockTransactions_differentPrices_correctlyProcessesBasedOnQuantityOnly() {
        Long userId = 1L;
        String stockSymbol = "AAPL";

        StockTransaction buy1 = createTransactionWithPrice(BUY, 10.0, 150.0, baseTime);
        StockTransaction buy2 = createTransactionWithPrice(BUY, 15.0, 200.0, baseTime.plusMinutes(10));
        StockTransaction sell1 = createTransactionWithPrice(SELL, 8.0, 180.0, baseTime.plusMinutes(20));
        StockTransaction buy3 = createTransactionWithPrice(BUY, 5.0, 175.0, baseTime.plusMinutes(30));
        StockTransaction sell2 = createTransactionWithPrice(SELL, 12.0, 190.0, baseTime.plusMinutes(40));

        when(stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol))
                .thenReturn(Arrays.asList(buy1, buy2, sell1, buy3, sell2));

        List<StockTransaction> result = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getQuantity()).isEqualTo(5.0);
        assertThat(result.get(0).getPrice()).isEqualTo(200.0);
        assertThat(result.get(1).getQuantity()).isEqualTo(5.0);
        assertThat(result.get(1).getPrice()).isEqualTo(175.0);
    }

    @Test
    void getUnsoldStockTransactions_unknownTransactionType_throwsIllegalArgumentException() {
        Long userId = 1L;
        String stockSymbol = "AAPL";

        StockTransaction invalidTransaction = createTransaction(null, 10.0, baseTime);

        when(stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol))
                .thenReturn(List.of(invalidTransaction));

        assertThatThrownBy(() -> unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown transaction type: null");
    }

    @Test
    void getUnsoldStockTransactions_zeroQuantityTransactions_handlesCorrectly() {
        Long userId = 1L;
        String stockSymbol = "AAPL";

        StockTransaction buy1 = createTransaction(BUY, 10.0, baseTime);
        StockTransaction buy2 = createTransaction(BUY, 0.0, baseTime.plusMinutes(5));
        StockTransaction sell = createTransaction(SELL, 5.0, baseTime.plusMinutes(10));

        when(stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol))
                .thenReturn(Arrays.asList(buy1,buy2,  sell));

        List<StockTransaction> result = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuantity()).isEqualTo(5.0);
    }

    @Test
    void getUnsoldStockTransactions_sellWithZeroQuantity_doesNotAffectBuys() {
        Long userId = 1L;
        String stockSymbol = "AAPL";

        StockTransaction buy = createTransaction(BUY, 10.0, baseTime);
        StockTransaction sell = createTransaction(SELL, 0.0, baseTime.plusMinutes(10));

        when(stockTransactionRepository.findByUserIdAndStockSymbolOrderByTimeOfTransactionAsc(userId, stockSymbol))
                .thenReturn(Arrays.asList(buy, sell));

        List<StockTransaction> result = unsoldStockTransactionsService.getUnsoldStockTransactions(userId, stockSymbol);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuantity()).isEqualTo(10.0);
    }

    private StockTransaction createTransaction(TransactionType type, Double quantity, LocalDateTime time) {
        StockTransaction transaction = new StockTransaction();
        transaction.setType(type);
        transaction.setQuantity(quantity);
        transaction.setTimeOfTransaction(time);
        transaction.setUser(user);
        transaction.setStock(stock);
        transaction.setPrice(100.0);
        transaction.setCurrency("USD");
        return transaction;
    }

    private StockTransaction createTransactionWithPrice(TransactionType type, Double quantity, Double price, LocalDateTime time) {
        StockTransaction transaction = new StockTransaction();
        transaction.setType(type);
        transaction.setQuantity(quantity);
        transaction.setTimeOfTransaction(time);
        transaction.setUser(user);
        transaction.setStock(stock);
        transaction.setPrice(price);
        transaction.setCurrency("USD");
        return transaction;
    }
}
