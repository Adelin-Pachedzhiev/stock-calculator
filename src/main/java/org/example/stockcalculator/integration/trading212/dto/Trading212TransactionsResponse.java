package org.example.stockcalculator.integration.trading212.dto;

import java.util.List;

public record Trading212TransactionsResponse(
        List<Trading212Transaction> items,
        String nextPagePath) {

}
