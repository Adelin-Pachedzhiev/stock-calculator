package org.example.stockcalculator.integration;

import org.example.stockcalculator.entity.Platform;
import org.example.stockcalculator.entity.PlatformIntegration;

public interface TransactionsSyncService {

    void syncTransactions(PlatformIntegration integration);

    Platform relevantPlatform();
}
