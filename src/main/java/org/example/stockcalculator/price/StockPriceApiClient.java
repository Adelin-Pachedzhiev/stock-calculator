package org.example.stockcalculator.price;

import java.util.Optional;

import org.example.stockcalculator.portfolio.dto.StockPriceResponse;

public interface StockPriceApiClient {

    Optional<StockPriceResponse> getPriceForSymbol(String symbol);
}
