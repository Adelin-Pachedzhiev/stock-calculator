package org.example.stockcalculator.stock;

import org.example.stockcalculator.stock.profit.dto.StockInvestmentProfitInfo;
import org.example.stockcalculator.stock.profit.dto.StockProfit;
import org.example.stockcalculator.stock.profit.service.StockProfitService;
import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.price.repository.StockPriceRepository;
import org.example.stockcalculator.stock.repository.StockRepository;
import org.example.stockcalculator.transaction.repository.StockTransactionRepository;
import org.example.stockcalculator.transaction.service.UnsoldStockTransactionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class StockProfitServiceTest {

    @Mock
    private StockTransactionRepository stockTransactionRepository;

    @Mock
    private StockPriceRepository stockPriceRepository;

    @Mock
    private UnsoldStockTransactionsService unsoldStockTransactionsService;

    @Mock
    private StockRepository stockRepository;

    private StockProfitService stockProfitService;

    private static final Long TEST_USER_ID = 1L;
    private static final String APPLE_SYMBOL = "AAPL";
    private static final String GOOGLE_SYMBOL = "GOOGL";
    private static final double PRECISION = 0.0001;

    @BeforeEach
    void setUp() {
        stockProfitService = new StockProfitService(
            stockTransactionRepository,
            stockPriceRepository,
            unsoldStockTransactionsService,
            stockRepository
        );
    }

    @Nested
    class CalculateProfitBySymbolTests {

        @Test
        void shouldCalculateProfitForSingleStockSingleTransaction() {
            Stock stock = createStock(APPLE_SYMBOL);
            StockTransaction transaction = createTransaction(100.0, 2.0, 1.0);
            StockPriceEntity currentPrice = createStockPrice(120.0);

            when(stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(TEST_USER_ID))
                .thenReturn(List.of(stock));
            when(unsoldStockTransactionsService.getUnsoldStockTransactions(TEST_USER_ID, APPLE_SYMBOL))
                .thenReturn(List.of(transaction));
            when(stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(APPLE_SYMBOL))
                .thenReturn(currentPrice);

            Map<String, StockProfit> result = stockProfitService.calculateProfitBySymbol(TEST_USER_ID);

            assertThat(result).hasSize(1);
            StockProfit profit = result.get(APPLE_SYMBOL);

            assertThat(profit).isNotNull();
            assertThat(profit.profit()).isCloseTo(39.0, within(PRECISION)); // (120-100)*2 - 1 = 39
            assertThat(profit.profitPercentage()).isCloseTo(19.5, within(PRECISION)); // 39/200*100
            assertThat(profit.currentValue()).isCloseTo(240.0, within(PRECISION)); // 120*2
            assertThat(profit.investedAmountInUsd()).isCloseTo(201.0, within(PRECISION)); // 100*2+1
            assertThat(profit.totalShares()).isCloseTo(2.0, within(PRECISION));
        }

        @Test
        void shouldReturnEmptyMapWhenNoTransactionsExist() {
            Stock stock = createStock(APPLE_SYMBOL);

            when(stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(TEST_USER_ID))
                .thenReturn(List.of(stock));
            when(unsoldStockTransactionsService.getUnsoldStockTransactions(TEST_USER_ID, APPLE_SYMBOL))
                .thenReturn(Collections.emptyList());
            when(stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(APPLE_SYMBOL))
                .thenReturn(null);

            Map<String, StockProfit> result = stockProfitService.calculateProfitBySymbol(TEST_USER_ID);

            assertThat(result).isEmpty();
        }

        @Test
        void shouldCalculateProfitForMultipleTransactions() {
            // Given
            Stock stock = createStock(APPLE_SYMBOL);
            StockTransaction transaction1 = createTransaction(100.0, 1.0, 1.0);
            StockTransaction transaction2 = createTransaction(110.0, 2.0, 2.0);
            StockPriceEntity currentPrice = createStockPrice(120.0);

            when(stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(TEST_USER_ID))
                .thenReturn(List.of(stock));
            when(unsoldStockTransactionsService.getUnsoldStockTransactions(TEST_USER_ID, APPLE_SYMBOL))
                .thenReturn(List.of(transaction1, transaction2));
            when(stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(APPLE_SYMBOL))
                .thenReturn(currentPrice);

            Map<String, StockProfit> result = stockProfitService.calculateProfitBySymbol(TEST_USER_ID);

            StockProfit profit = result.get(APPLE_SYMBOL);
            assertThat(profit).isNotNull();

            // profit: (120-100)*1-1 + (120-110)*2-2 = 19 + 18 = 37
            assertThat(profit.profit()).isCloseTo(37.0, within(PRECISION));
            // invested: 100*1+1 + 110*2+2 = 101 + 222 = 323
            assertThat(profit.investedAmountInUsd()).isCloseTo(323.0, within(PRECISION));
            // shares: 1+2=3
            assertThat(profit.totalShares()).isCloseTo(3.0, within(PRECISION));
            // current value: 120*3=360
            assertThat(profit.currentValue()).isCloseTo(360.0, within(PRECISION));
            // profit percent: 37/(100*1+110*2)*100 = 37/320*100 = 11.5625
            assertThat(profit.profitPercentage()).isCloseTo(11.5625, within(PRECISION));
        }

        @Test
        void shouldFilterStocksWithZeroShares() {
            Stock appleStock = createStock(APPLE_SYMBOL);
            Stock googleStock = createStock(GOOGLE_SYMBOL);
            StockTransaction transaction = createTransaction(100.0, 1.0, 1.0);
            StockPriceEntity price = createStockPrice(110.0);

            when(stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(TEST_USER_ID))
                .thenReturn(List.of(appleStock, googleStock));
            when(unsoldStockTransactionsService.getUnsoldStockTransactions(TEST_USER_ID, APPLE_SYMBOL))
                .thenReturn(List.of(transaction));
            when(unsoldStockTransactionsService.getUnsoldStockTransactions(TEST_USER_ID, GOOGLE_SYMBOL))
                .thenReturn(Collections.emptyList());
            when(stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(APPLE_SYMBOL))
                .thenReturn(price);
            when(stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(GOOGLE_SYMBOL))
                .thenReturn(null);

            Map<String, StockProfit> result = stockProfitService.calculateProfitBySymbol(TEST_USER_ID);

            assertThat(result).hasSize(1);
            assertThat(result).containsKey(APPLE_SYMBOL);
            assertThat(result).doesNotContainKey(GOOGLE_SYMBOL);
        }
    }

    @Nested
    class CalculateTotalProfitTests {

        @Test
        void shouldAggregateProfitsAcrossMultipleStocks() {
            Stock appleStock = createStock(APPLE_SYMBOL);
            Stock googleStock = createStock(GOOGLE_SYMBOL);

            StockTransaction appleTx = createTransaction(100.0, 2.0, 1.0);
            StockTransaction googleTx = createTransaction(200.0, 1.0, 2.0);

            StockPriceEntity applePrice = createStockPrice(120.0);
            StockPriceEntity googlePrice = createStockPrice(220.0);

            when(stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(TEST_USER_ID))
                .thenReturn(List.of(appleStock, googleStock));
            when(unsoldStockTransactionsService.getUnsoldStockTransactions(TEST_USER_ID, APPLE_SYMBOL))
                .thenReturn(List.of(appleTx));
            when(unsoldStockTransactionsService.getUnsoldStockTransactions(TEST_USER_ID, GOOGLE_SYMBOL))
                .thenReturn(List.of(googleTx));
            when(stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(APPLE_SYMBOL))
                .thenReturn(applePrice);
            when(stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(GOOGLE_SYMBOL))
                .thenReturn(googlePrice);

            StockProfit totalProfit = stockProfitService.calculateTotalProfit(TEST_USER_ID);

            // Total profit: AAPL: (120-100)*2-1=39, GOOGL: (220-200)*1-2=18, Total: 57
            assertThat(totalProfit.profit()).isCloseTo(57.0, within(PRECISION));
            // Total invested: AAPL: 201, GOOGL: 202, Total: 403
            assertThat(totalProfit.investedAmountInUsd()).isCloseTo(403.0, within(PRECISION));
            // Total shares: AAPL: 2, GOOGL: 1, Total: 3
            assertThat(totalProfit.totalShares()).isCloseTo(3.0, within(PRECISION));
            // Total current value: AAPL: 240, GOOGL: 220, Total: 460
            assertThat(totalProfit.currentValue()).isCloseTo(460.0, within(PRECISION));
            // Average profit percentage: (19.5 + 9.0) / 2 = 14.25
            assertThat(totalProfit.profitPercentage()).isCloseTo(14.25, within(PRECISION));
        }
    }

    @Nested
    class CalculateProfitForSymbolTests {

        @Test
        void shouldCalculateProfitForSpecificSymbolUsingCurrentUserId() {
            StockTransaction transaction = createTransaction(100.0, 1.0, 1.0);
            StockPriceEntity price = createStockPrice(110.0);

            when(unsoldStockTransactionsService.getUnsoldStockTransactions(TEST_USER_ID, APPLE_SYMBOL))
                .thenReturn(List.of(transaction));
            when(stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(APPLE_SYMBOL))
                .thenReturn(price);

            try (MockedStatic<org.example.stockcalculator.account.utils.AuthUtils> authUtils =
                 mockStatic(org.example.stockcalculator.account.utils.AuthUtils.class)) {
                authUtils.when(org.example.stockcalculator.account.utils.AuthUtils::currentUserId)
                    .thenReturn(TEST_USER_ID);

                StockProfit result = stockProfitService.calculateProfitForSymbol(APPLE_SYMBOL);

                assertThat(result.profit()).isCloseTo(9.0, within(PRECISION)); // (110-100)*1-1 = 9
                assertThat(result.profitPercentage()).isCloseTo(9.0, within(PRECISION)); // 9/100*100 = 9
                assertThat(result.currentValue()).isCloseTo(110.0, within(PRECISION));
                assertThat(result.investedAmountInUsd()).isCloseTo(101.0, within(PRECISION));
                assertThat(result.totalShares()).isCloseTo(1.0, within(PRECISION));
            }
        }
    }

    @Nested
    class CalculateProfitInfoBySymbolTests {

        @Test
        void shouldReturnProfitInfoWithStockEntityAndProfitData() {
            Stock stock = createStockWithName(APPLE_SYMBOL, "Apple Inc.");
            StockTransaction transaction = createTransaction(150.0, 2.0, 2.0);
            StockPriceEntity price = createStockPrice(160.0);

            when(stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(TEST_USER_ID))
                .thenReturn(List.of(stock));
            when(unsoldStockTransactionsService.getUnsoldStockTransactions(TEST_USER_ID, APPLE_SYMBOL))
                .thenReturn(List.of(transaction));
            when(stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(APPLE_SYMBOL))
                .thenReturn(price);
            when(stockRepository.findBySymbol(APPLE_SYMBOL))
                .thenReturn(Optional.of(stock));

            try (MockedStatic<org.example.stockcalculator.account.utils.AuthUtils> authUtils =
                 mockStatic(org.example.stockcalculator.account.utils.AuthUtils.class)) {
                authUtils.when(org.example.stockcalculator.account.utils.AuthUtils::currentUserId)
                    .thenReturn(TEST_USER_ID);

                List<StockInvestmentProfitInfo> result = stockProfitService.calculateProfitInfoBySymbol();

                assertThat(result).hasSize(1);
                StockInvestmentProfitInfo info = result.getFirst();
                assertThat(info.stock()).isEqualTo(stock);
                assertThat(info.stockProfit().profit()).isCloseTo(18.0, within(PRECISION)); // (160-150)*2-2 = 18
                assertThat(info.stockProfit().profitPercentage()).isCloseTo(6.0, within(PRECISION)); // 18/300*100 = 6
                assertThat(info.stockProfit().currentValue()).isCloseTo(320.0, within(PRECISION)); // 160*2
                assertThat(info.stockProfit().investedAmountInUsd()).isCloseTo(302.0, within(PRECISION)); // 150*2+2
                assertThat(info.stockProfit().totalShares()).isCloseTo(2.0, within(PRECISION));
            }
        }
    }

    @Nested
    class EdgeCasesTests {

        @Test
        void shouldHandleZeroProfitPercentageWhenInvestmentIsZero() {
            Stock stock = createStock(APPLE_SYMBOL);
            StockTransaction transaction = createTransaction(0.0, 1.0, 0.0); // Zero price
            StockPriceEntity price = createStockPrice(10.0);

            when(stockTransactionRepository.findStockSymbolsOfTransactionsByUserId(TEST_USER_ID))
                .thenReturn(List.of(stock));
            when(unsoldStockTransactionsService.getUnsoldStockTransactions(TEST_USER_ID, APPLE_SYMBOL))
                .thenReturn(List.of(transaction));
            when(stockPriceRepository.findTopByStockSymbolOrderByTimestampDesc(APPLE_SYMBOL))
                .thenReturn(price);

            Map<String, StockProfit> result = stockProfitService.calculateProfitBySymbol(TEST_USER_ID);

            StockProfit profit = result.get(APPLE_SYMBOL);
            assertThat(profit).isNotNull();
            assertThat(profit.profit()).isCloseTo(10.0, within(PRECISION)); // (10-0)*1-0 = 10
            assertThat(profit.profitPercentage()).isCloseTo(0.0, within(PRECISION)); // Should be 0 when totalInvested is 0
            assertThat(profit.currentValue()).isCloseTo(10.0, within(PRECISION));
            assertThat(profit.investedAmountInUsd()).isCloseTo(0.0, within(PRECISION));
            assertThat(profit.totalShares()).isCloseTo(1.0, within(PRECISION));
        }
    }

    private Stock createStock(String symbol) {
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        return stock;
    }

    private Stock createStockWithName(String symbol, String name) {
        Stock stock = createStock(symbol);
        stock.setName(name);
        return stock;
    }

    private StockTransaction createTransaction(double price, double quantity, double fee) {
        StockTransaction transaction = new StockTransaction();
        transaction.setPrice(price);
        transaction.setQuantity(quantity);
        transaction.setFee(fee);
        return transaction;
    }

    private StockPriceEntity createStockPrice(double price) {
        StockPriceEntity priceEntity = new StockPriceEntity();
        priceEntity.setPrice(price);
        return priceEntity;
    }
}
