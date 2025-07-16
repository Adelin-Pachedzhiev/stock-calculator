package org.example.stockcalculator.integration.trading212;

import static org.example.stockcalculator.auth.utils.AuthUtils.currentUserId;
import static org.example.stockcalculator.entity.Platform.TRADING212;
import static org.example.stockcalculator.entity.TransactionType.BUY;
import static org.example.stockcalculator.entity.TransactionType.SELL;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.stockcalculator.entity.IntegrationSecret;
import org.example.stockcalculator.entity.Platform;
import org.example.stockcalculator.entity.PlatformIntegration;
import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.integration.TransactionsSyncService;
import org.example.stockcalculator.integration.trading212.dto.Trading212InstrumentMetadata;
import org.example.stockcalculator.integration.trading212.dto.Trading212Transaction;
import org.example.stockcalculator.integration.trading212.dto.Trading212UserInfo;
import org.example.stockcalculator.integration.trading212.dto.TransactionTax;
import org.example.stockcalculator.integration.repository.PlatformIntegrationJpaRepository;
import org.example.stockcalculator.repository.StockRepository;
import org.example.stockcalculator.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class Trading212TransactionsSyncService implements TransactionsSyncService {

    private static final double EUR_USD_RATE = 1.17;
    private final Trading212ApiClient apiClient;
    private final StockTransactionRepository stockTransactionRepository;
    private final PlatformIntegrationJpaRepository platformIntegrationRepository;
    private final StockRepository stockRepository;

    @Override
    public void syncTransactions(PlatformIntegration integration) {
        IntegrationSecret integrationSecret = integration.getSecret();
        String secret = integrationSecret.getSecret();

        Trading212UserInfo userInfo = apiClient.getUserInfo(secret);
        String mainCurrencyForAccount = userInfo.currencyCode();

        List<Trading212InstrumentMetadata> instrumentMetadata = apiClient.fetchInstrumentsMetadataForIntegration(secret);
        List<Stock> stocks = stockRepository.findAll();

        List<Trading212Transaction> trading212Transactions = apiClient.fetchTransactionsForIntegration(secret,
                integration.getLatestSyncedTransactionDate());

        List<StockTransaction> stockTransactions = trading212Transactions
                .stream()
                .flatMap(tx -> convertToStockTransactionEntity(tx, instrumentMetadata, stocks, mainCurrencyForAccount).stream())
                .peek(tx -> tx.setPlatformIntegration(new PlatformIntegration(integration.getId())))
                .toList();

        updatePlatformIntegration(trading212Transactions, integration);

        log.info("Saving {} stock transactions for integration ID {}", stockTransactions.size(), integration.getId());
        stockTransactionRepository.saveAll(stockTransactions);
    }

    private void updatePlatformIntegration(List<Trading212Transaction> trading212Transactions, PlatformIntegration platformIntegration) {
        if (!trading212Transactions.isEmpty()) {
            LocalDateTime localDateTimeOfLatestTransaction = trading212Transactions.getFirst().dateModified();
            platformIntegration.setLatestSyncedTransactionDate(localDateTimeOfLatestTransaction);
        }

        platformIntegration.setLastSyncAt(LocalDateTime.now());
        platformIntegrationRepository.save(platformIntegration);
    }

    private Optional<StockTransaction> convertToStockTransactionEntity(Trading212Transaction tx,
            List<Trading212InstrumentMetadata> instrumentMetadata,
            List<Stock> stocks,
            String mainCurrencyForAccount) {

        Trading212InstrumentMetadata stockMetadata = instrumentMetadata.stream()
                .filter(m -> m.ticker().equals(tx.ticker()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ticker " + tx.ticker() + " not found in metadata."));

        Optional<Stock> stock = stocks.stream()
                .filter(s -> s.getSymbol().equals(stockMetadata.shortName()))
                .findFirst();
        if (stock.isEmpty()) {
            return Optional.empty();
        }

        StockTransaction stockTransaction = new StockTransaction();
        stockTransaction.setStock(stock.get());
        stockTransaction.setUser(new UserAccount(currentUserId()));
        stockTransaction.setPrice(tx.fillPrice());
        stockTransaction.setFee(calculateFee(tx, mainCurrencyForAccount));
        stockTransaction.setTimeOfTransaction(tx.dateModified());
        stockTransaction.setCurrency(stockMetadata.currencyCode());

        computeQuantityAndType(tx, mainCurrencyForAccount, stockMetadata, stockTransaction);

        return Optional.of(stockTransaction);
    }

    private static void computeQuantityAndType(Trading212Transaction tx, String mainCurrencyForAccount, Trading212InstrumentMetadata stockMetadata,
            StockTransaction stockTransaction) {
        if (tx.orderedQuantity() == null) {
            double orderedValue = tx.orderedValue();
            if (!stockMetadata.currencyCode().equals(mainCurrencyForAccount)) {
                orderedValue *= EUR_USD_RATE;
            }
            stockTransaction.setQuantity(orderedValue / tx.fillPrice());
            stockTransaction.setType(tx.orderedValue() > 0 ? BUY : SELL);
        }
        else {
            stockTransaction.setQuantity(tx.orderedQuantity());
            stockTransaction.setType(tx.orderedQuantity() > 0 ? BUY : SELL);
            throw new UnsupportedOperationException("Ordered quantity is not supported yet.");
        }
    }

    private double calculateFee(Trading212Transaction tx, String mainCurrencyForAccount) {
        double taxAmount = -tx.taxes().stream().mapToDouble(TransactionTax::quantity).sum();
        if (mainCurrencyForAccount.equals("EUR")) {
            taxAmount *= EUR_USD_RATE;
        }
        return taxAmount;
    }

    @Override
    public Platform relevantPlatform() {
        return TRADING212;
    }
}
