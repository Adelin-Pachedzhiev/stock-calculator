package org.example.stockcalculator.integration.plaid.service;

import static org.example.stockcalculator.account.utils.AuthUtils.currentUserId;
import static org.example.stockcalculator.entity.Platform.PLAID;
import static org.example.stockcalculator.entity.TransactionType.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.stockcalculator.entity.Platform;
import org.example.stockcalculator.entity.PlatformIntegration;
import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.entity.TransactionType;
import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.integration.TransactionsSyncService;
import org.example.stockcalculator.stock.repository.StockRepository;
import org.example.stockcalculator.transaction.repository.StockTransactionRepository;
import org.springframework.stereotype.Component;

import com.plaid.client.model.Holding;
import com.plaid.client.model.InvestmentsHoldingsGetRequest;
import com.plaid.client.model.InvestmentsTransactionsGetRequest;
import com.plaid.client.model.Security;
import com.plaid.client.request.PlaidApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaidTransactionsSyncService implements TransactionsSyncService {

    private final PlaidApi plaidApi;
    private final StockRepository stockRepository;
    private final StockTransactionRepository stockTransactionRepository;

    @Override
    public void syncTransactions(PlatformIntegration integration) {
        String accessToken = integration.getSecret().getSecret();
        InvestmentsHoldingsGetRequest request = new InvestmentsHoldingsGetRequest();
        request.accessToken(accessToken);

        List<Stock> stocks = stockRepository.findAll();

        try {
            var response = plaidApi.investmentsHoldingsGet(request).execute();
            log.info("InvestmentsHoldingsGetResponse {}", response.body());

            List<Holding> holdings = response.body().getHoldings();
            List<Security> securities = response.body().getSecurities();

            Set<StockTransaction> stockTransactions = new HashSet<>();
            holdings.forEach(h -> {
                Security security = securities.stream()
                        .filter(s -> s.getSecurityId().equals(h.getSecurityId()))
                        .findFirst()
                        .orElseThrow();

                Optional<Stock> stockOpt = stocks.stream()
                        .filter(s -> s.getSymbol().equals(security.getTickerSymbol()))
                        .findFirst();
                if (stockOpt.isEmpty()){
                    return;
                }

                Stock stock = stockOpt.get();
                StockTransaction transaction = new StockTransaction();
                transaction.setStock(stock);
                transaction.setTimeOfTransaction(LocalDateTime.now());
                transaction.setPlatformIntegration(integration);
                transaction.setType(BUY);
                transaction.setQuantity(h.getQuantity());
                transaction.setCurrency(security.getIsoCurrencyCode());
                transaction.setUser(new UserAccount(currentUserId()));
                transaction.setFee(0.0);
                double buyPrice = h.getCostBasis() / h.getQuantity();
                transaction.setPrice(buyPrice);

                stockTransactions.add(transaction);
            });

            stockTransactionRepository.saveAll(stockTransactions);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Platform relevantPlatform() {
        return PLAID;
    }
}
