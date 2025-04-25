package org.example.stockcalculator.service;

import java.util.Optional;

import org.example.stockcalculator.model.StockPrice;

public interface StockPriceApiClient {

    Optional<StockPrice> getPriceForSymbol(String symbol);
}
