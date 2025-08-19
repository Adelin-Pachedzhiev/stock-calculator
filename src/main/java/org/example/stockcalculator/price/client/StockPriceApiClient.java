package org.example.stockcalculator.price.client;

import java.util.Optional;

import org.example.stockcalculator.portfolio.dto.StockPriceResponse;

public interface StockPriceApiClient {

    Optional<StockPriceResponse> getPriceForSymbol(String symbol);

    boolean isSymbolSupported(String symbol);

}
