package org.example.stockcalculator.price.client;

import static org.example.stockcalculator.util.ResilientApiCallUtil.executeWithRetryOn429;
import static org.springframework.http.HttpMethod.GET;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.example.stockcalculator.config.StockApiProperties;
import org.example.stockcalculator.portfolio.dto.StockPriceResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class FinnhubStockPriceApiClient implements StockPriceApiClient {

    private final RestTemplate restTemplate;
    private final StockApiProperties stockApiProperties;

    @Override
    public Optional<StockPriceResponse> getPriceForSymbol(String symbol) {

        String url = UriComponentsBuilder.fromUriString(stockApiProperties.url())
                .path("/quote")
                .queryParam("symbol", symbol)
                .queryParam("token", stockApiProperties.apiKey())
                .encode().toUriString();
        try {
            StockPriceResponse response = executeWithRetryOn429(() -> restTemplate.getForObject(url, StockPriceResponse.class), "/quote");
            return Optional.ofNullable(response);
        }
        catch (RestClientException e) {
            log.warn("Failed to retrieve stock price for symbol: {}", symbol, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean isSymbolSupported(String symbol) {
        URI uri = UriComponentsBuilder.fromUriString(stockApiProperties.url())
                .path("/search")
                .queryParam("q", symbol)
                .queryParam("token", stockApiProperties.apiKey())
                .encode()
                .build()
                .toUri();

        StockSearchResponse stockSearchResponse = executeWithRetryOn429(() -> getStockSearchResponses(uri), stockApiProperties.url());

        return stockSearchResponse.result().stream()
                .anyMatch(res -> res.symbol().equals(symbol));
    }

    private StockSearchResponse getStockSearchResponses(URI uri) {
        return restTemplate.exchange(new RequestEntity<>(GET, uri),
                new ParameterizedTypeReference<StockSearchResponse>() {

                }).getBody();
    }

    private record StockSearchResponse(List<StockSearchResponseResult> result) {

    }

    private record StockSearchResponseResult(String symbol) {

    }
}
