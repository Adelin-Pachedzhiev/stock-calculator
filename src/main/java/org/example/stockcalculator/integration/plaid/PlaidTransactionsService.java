package org.example.stockcalculator.integration.plaid;

import org.springframework.stereotype.Component;

import com.plaid.client.ApiClient;
import com.plaid.client.model.LinkTokenCreateRequest;
import com.plaid.client.request.PlaidApi;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlaidTransactionsService {

    private final ApiClient plaidApiClient;

    @PostConstruct
    public void init() {
        PlaidApi service = plaidApiClient.createService(PlaidApi.class);
        service.linkTokenCreate(new LinkTokenCreateRequest());
    }
}
