package org.example.stockcalculator.integration;

import static org.example.stockcalculator.auth.utils.AuthUtils.currentUserId;
import static org.example.stockcalculator.entity.TransactionType.BUY;
import static org.example.stockcalculator.entity.TransactionType.SELL;

import java.util.List;
import java.util.Optional;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.entity.UserIntegrationSecret;
import org.example.stockcalculator.integration.trading212.Trading212ApiClient;
import org.example.stockcalculator.integration.trading212.dto.Trading212InstrumentMetadata;
import org.example.stockcalculator.integration.trading212.dto.Trading212Transaction;
import org.example.stockcalculator.integration.trading212.dto.Trading212UserInfo;
import org.example.stockcalculator.integration.trading212.dto.TransactionTax;
import org.example.stockcalculator.repository.IntegrationSecretRepository;
import org.example.stockcalculator.repository.StockRepository;
import org.example.stockcalculator.repository.StockTransactionRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockTransactionManager {

    public static final double EUR_USD_RATE = 1.17;
    private final Trading212ApiClient apiClient;
    private final StockTransactionRepository stockTransactionRepository;
    private final IntegrationSecretRepository integrationSecretRepository;
    private final StockRepository stockRepository;

    public void syncTransactionsToDbForIntegration(Long integrationId) {
        Optional<UserIntegrationSecret> integrationOpt = integrationSecretRepository.findById(integrationId);
        if (integrationOpt.isEmpty()) {
            throw new IllegalArgumentException("Integration with ID " + integrationId + " not found.");
        }
        UserIntegrationSecret integration = integrationOpt.get();
        String secret = integration.getSecret();

        Trading212UserInfo userInfo = apiClient.getUserInfo(secret);
        String mainCurrencyForAccount = userInfo.currencyCode();

        List<Trading212InstrumentMetadata> instrumentMetadata = apiClient.fetchInstrumentsMetadataForIntegration(secret);
        List<Stock> stocks = stockRepository.findAll();

        List<StockTransaction> stockTransactions = apiClient.fetchTransactionsForIntegration(secret)
                .stream()
                .flatMap(tx -> convertToStockTransactionEntity(tx, instrumentMetadata, stocks, mainCurrencyForAccount).stream())
                .toList();

        log.info("Saving {} stock transactions for integration ID {}", stockTransactions.size(), integrationId);
        stockTransactionRepository.saveAll(stockTransactions);
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
            stockTransaction.setQuantity(orderedValue  / tx.fillPrice());
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
            taxAmount*= EUR_USD_RATE;
        }
        return taxAmount;
    }
}
