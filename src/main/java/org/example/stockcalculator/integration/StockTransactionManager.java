package org.example.stockcalculator.integration;

import static org.example.stockcalculator.integration.InstitutionNameConstants.TRADING212;

import org.example.stockcalculator.entity.PlatformIntegration;
import org.example.stockcalculator.integration.plaid.PlaidTransactionsSyncService;
import org.example.stockcalculator.integration.trading212.Trading212TransactionsSyncService;
import org.example.stockcalculator.integration.repository.PlatformIntegrationJpaRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockTransactionManager {

    private final PlatformIntegrationJpaRepository platformIntegrationRepository;
    private final PlaidTransactionsSyncService plaidTransactionsSyncService;
    private final Trading212TransactionsSyncService trading212TransactionsSyncService;

    public void syncTransactionsToDbForIntegration(Long integrationId) {
        PlatformIntegration platformIntegration = platformIntegrationRepository.findById(integrationId).orElseThrow();
        if (platformIntegration.getPlatform().equals(TRADING212)) {
            trading212TransactionsSyncService.syncTransactions(platformIntegration);
        }
        else {
            plaidTransactionsSyncService.syncTransactions(platformIntegration);
        }
    }
}
