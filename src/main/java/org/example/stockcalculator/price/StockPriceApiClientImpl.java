package org.example.stockcalculator.price;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockcalculator.config.StockApiProperties;
import org.example.stockcalculator.portfolio.dto.StockPriceResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class StockPriceApiClientImpl implements StockPriceApiClient{

    private final RestTemplate restTemplate;
    private final StockApiProperties stockApiProperties;

    public Optional<StockPriceResponse> getPriceForSymbol(String symbol) {

        String url = UriComponentsBuilder.fromUriString(stockApiProperties.url())
                .path("/quote")
                .queryParam("symbol", symbol)
                .queryParam("token", stockApiProperties.apiKey())
                .encode().toUriString();
        try {
            StockPriceResponse response = restTemplate.getForObject(url, StockPriceResponse.class);
            return Optional.ofNullable(response);
        } catch (RestClientException e) {
            log.warn("Failed to retrieve stock price for symbol: {}", symbol, e);
            return Optional.empty();
        }
    }
}
