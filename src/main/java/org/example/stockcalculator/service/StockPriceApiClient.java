package org.example.stockcalculator.service;

import java.util.Optional;

import org.example.stockcalculator.model.StockPriceResponse;

public interface StockPriceApiClient {

    Optional<StockPriceResponse> getPriceForSymbol(String symbol);
}
