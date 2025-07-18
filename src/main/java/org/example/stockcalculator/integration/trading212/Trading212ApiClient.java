package org.example.stockcalculator.integration.trading212;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.stockcalculator.config.StockTransactionApiProperties;
import org.example.stockcalculator.integration.trading212.dto.Trading212InstrumentMetadata;
import org.example.stockcalculator.integration.trading212.dto.Trading212Transaction;
import org.example.stockcalculator.integration.trading212.dto.Trading212TransactionsResponse;
import org.example.stockcalculator.integration.trading212.dto.Trading212UserInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Trading212ApiClient {

    private static final String TRANSACTIONS_PATH = "/api/v0/equity/history/orders";
    private static final String INSTRUMENTS_METADATA_PATH = "/api/v0/equity/metadata/instruments";
    private static final String USER_ACCOUNT_INFO_PATH = "/api/v0/equity/account/info";
    private final RestTemplate restTemplate;
    private final StockTransactionApiProperties apiProperties;

    public List<Trading212Transaction> fetchTransactionsForIntegration(String secret, LocalDateTime toDate) {
        String currentPath = TRANSACTIONS_PATH + "?limit=50";
        List<Trading212Transaction> transactions = new ArrayList<>();

        boolean hasReachedToDate;
        do {
            Trading212TransactionsResponse exchange = getTransactionsFromPath(currentPath, secret);
            log.info("Fetched {} transactions from Trading212.", exchange);
            hasReachedToDate = filterOldTransactionsAndCheckIfReachedToDate(toDate, exchange);
            currentPath = exchange.nextPagePath();
            transactions.addAll(exchange.items());
        }
        while (currentPath != null && !hasReachedToDate);

        return transactions;
    }

    private  boolean filterOldTransactionsAndCheckIfReachedToDate(LocalDateTime toDate, Trading212TransactionsResponse transactions) {
        if (toDate == null) {
            return false;
        }

        List<Trading212Transaction> filteredByToDateTransactions = transactions.items().stream()
                .filter(tx -> tx.dateModified().isAfter(toDate))
                .toList();
        if (transactions.items().size() != filteredByToDateTransactions.size()) {
            transactions.items().retainAll(filteredByToDateTransactions);
            return true;
        }
        return false;
    }

    public List<Trading212InstrumentMetadata> fetchInstrumentsMetadataForIntegration(String secret) {
        HttpHeaders headers = createHeadersWithSecret(secret);

        String url = apiProperties.url() + INSTRUMENTS_METADATA_PATH;

        return restTemplate.exchange(url, GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Trading212InstrumentMetadata>>() {

                })
                .getBody();
    }

    public Trading212UserInfo getUserInfo(String secret) {
        HttpHeaders headers = createHeadersWithSecret(secret);

        String url = apiProperties.url() + USER_ACCOUNT_INFO_PATH;

        return restTemplate.exchange(url, GET, new HttpEntity<>(headers), Trading212UserInfo.class).getBody();
    }

    private Trading212TransactionsResponse getTransactionsFromPath(String path, String secret) {
        HttpHeaders headers = createHeadersWithSecret(secret);

        String url = apiProperties.url() + path;

        return restTemplate.exchange(url, GET, new HttpEntity<>(headers), Trading212TransactionsResponse.class).getBody();
    }

    @NotNull
    private HttpHeaders createHeadersWithSecret(String secret) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, secret);
        return headers;
    }
}
