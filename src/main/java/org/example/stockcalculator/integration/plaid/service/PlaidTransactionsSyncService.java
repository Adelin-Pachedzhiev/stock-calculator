package org.example.stockcalculator.integration.plaid.service;

import static org.example.stockcalculator.entity.Platform.PLAID;

import org.apache.commons.lang3.NotImplementedException;
import org.example.stockcalculator.entity.Platform;
import org.example.stockcalculator.entity.PlatformIntegration;
import org.example.stockcalculator.integration.TransactionsSyncService;
import org.springframework.stereotype.Component;

@Component
public class PlaidTransactionsSyncService implements TransactionsSyncService {

    @Override
    public void syncTransactions(PlatformIntegration integration) {
        throw new NotImplementedException();
    }

    @Override
    public Platform relevantPlatform() {
        return PLAID;
    }
}
