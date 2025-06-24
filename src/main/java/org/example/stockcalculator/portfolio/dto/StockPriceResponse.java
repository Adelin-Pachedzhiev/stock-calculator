package org.example.stockcalculator.portfolio.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record StockPriceResponse(
        @JsonProperty("c") double price,
        @JsonProperty("d") double change,
        @JsonProperty("dp") double changePercent,
        @JsonProperty("h") double highPrice,
        @JsonProperty("l") double lowPrice,
        @JsonProperty("o") double openPrice,
        @JsonProperty("pc") double previousClosePrice) {
}
