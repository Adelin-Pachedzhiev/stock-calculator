package org.example.stockcalculator.integration.plaid.service;

import static com.plaid.client.model.CountryCode.CA;
import static com.plaid.client.model.CountryCode.US;
import static org.example.stockcalculator.account.utils.AuthUtils.currentUserId;

import java.io.IOException;
import java.util.List;

import org.example.stockcalculator.integration.repository.PlatformIntegrationRepository;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.plaid.client.model.ItemGetRequest;
import com.plaid.client.model.ItemGetResponse;
import com.plaid.client.model.ItemPublicTokenExchangeRequest;
import com.plaid.client.model.LinkTokenCreateRequest;
import com.plaid.client.model.LinkTokenCreateRequestUser;
import com.plaid.client.model.LinkTokenCreateResponse;
import com.plaid.client.model.Products;
import com.plaid.client.request.PlaidApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaidLinkService {

    private final PlaidApi plaidApi;
    private final PlatformIntegrationRepository platformIntegrationRepository;

    public String createLinkToken()
            throws IOException {
        Long userId = currentUserId();

        LinkTokenCreateRequestUser user = new LinkTokenCreateRequestUser()
                .clientUserId(String.valueOf(userId));

        LinkTokenCreateRequest request = new LinkTokenCreateRequest()
                .clientName("Portfolio Tracker")
                .user(user)
                .products(List.of(Products.INVESTMENTS))
                .language("en")
                .countryCodes(List.of(US, CA));

        Response<LinkTokenCreateResponse> response = plaidApi.linkTokenCreate(request)
                .execute();
        throwOnFailedRequest(response);
        log.info("Link token created: {}", response.body());

        return response.body().getLinkToken();
    }

    public ResponseEntity<?> exchangePublicToken(String publicToken)
            throws IOException {
        String accessToken = exchangePublicForAccessToken(publicToken);

        String institutionName = fetchInstitutionName(accessToken);

        platformIntegrationRepository.saveIntegrationAndSecret(accessToken, currentUserId(), institutionName);

        return ResponseEntity.ok("Access token saved successfully");
    }

    private String exchangePublicForAccessToken(String publicToken)
            throws IOException {
        log.info("Exchanging public token: {}", publicToken);
        var itemPublicTokenExchangeRequest = new
                ItemPublicTokenExchangeRequest().publicToken(publicToken);

        var response = plaidApi.itemPublicTokenExchange(itemPublicTokenExchangeRequest).execute();
        throwOnFailedRequest(response);
        log.info("Exchange public token response: {}", response.body());

        return response.body().getAccessToken();
    }

    @Nullable
    private String fetchInstitutionName(String accessToken)
            throws IOException {
        ItemGetRequest itemGetRequest = new ItemGetRequest();
        itemGetRequest.accessToken(accessToken);
        Response<ItemGetResponse> response = plaidApi.itemGet(itemGetRequest).execute();
        throwOnFailedRequest(response);
        log.info("Institution retrieved: {}", response.body().getItem());

        return response.body().getItem().getInstitutionName();
    }

    private void throwOnFailedRequest(Response<?> response)
            throws IOException {
        if (!response.isSuccessful()) {
            log.error("Request failed: {}", response.errorBody());
            throw new RuntimeException(response.errorBody().string());
        }
    }
}
