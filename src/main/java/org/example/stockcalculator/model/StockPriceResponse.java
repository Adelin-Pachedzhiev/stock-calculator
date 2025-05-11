package org.example.stockcalculator.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public record StockPriceResponse(
        @JsonProperty("c") double currentPrice,
        @JsonProperty("d") double change,
        @JsonProperty("dp") double percentChange,
        @JsonProperty("h") double highPrice,
        @JsonProperty("l") double lowPrice,
        @JsonProperty("o") double openPrice,
        @JsonProperty("pc") double previousClosePrice) {
}
