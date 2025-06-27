package org.example.stockcalculator.integration.trading212.dto;

import java.time.LocalDateTime;

public record TransactionTax(
        String fillId,
        String name,
        Double quantity,
        LocalDateTime timeCharged) {


}
