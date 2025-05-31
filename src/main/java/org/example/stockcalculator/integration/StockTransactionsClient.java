package org.example.stockcalculator.integration;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.stockcalculator.config.StockTransactionApiProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockTransactionsClient {

    private static final String TRANSACTIONS_PATH = "/api/v0/equity/history/orders";
    private final RestTemplate restTemplate;
    private final StockTransactionApiProperties apiProperties;

    public List<Transaction> fetchTransactionsOfUser() { //        Long userId
        String currentPath = TRANSACTIONS_PATH;
        List<Transaction> transactions = new ArrayList<>();

        do {
            TransactionsResponse exchange = getTransactionsFromPath(currentPath);
            currentPath = exchange.nextPagePath();
            transactions.addAll(exchange.items());
        }
        while (currentPath != null);

        return transactions;
    }

    private TransactionsResponse getTransactionsFromPath(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, apiProperties.apiKey());

        String url = apiProperties.url() + path;

        return restTemplate.exchange(url, GET, new HttpEntity<>(headers), TransactionsResponse.class).getBody();
    }

    public record TransactionsResponse(
            List<Transaction> items,
            String nextPagePath) {

    }

    public record Transaction(
            String ticker,
            Double orderedValue,
            Double filledValue,
            LocalDateTime dateModified,
            Double fillPrice
    ) {

    }
}
