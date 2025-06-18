package org.example.stockcalculator.plaid;

import static com.plaid.client.model.CountryCode.CA;
import static com.plaid.client.model.CountryCode.US;
import static org.example.stockcalculator.auth.utils.AuthUtils.currentUserId;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plaid.client.model.ItemPublicTokenExchangeRequest;
import com.plaid.client.model.ItemPublicTokenExchangeResponse;
import com.plaid.client.model.LinkTokenCreateRequest;
import com.plaid.client.model.LinkTokenCreateRequestUser;
import com.plaid.client.model.LinkTokenCreateResponse;
import com.plaid.client.model.Products;
import com.plaid.client.request.PlaidApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Slf4j
@RestController
@RequestMapping("/api/plaid")
@RequiredArgsConstructor
public class LinkController {

    private final PlaidApi plaidApi;

    @PostMapping("/link-token")
    public Map<String, String> createLinkToken()
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
        log.info("Link token create response: {}", response);
        if (!response.isSuccessful()) {
            log.error("Failed to create link token: {}", response.errorBody().string());
            throw new IOException("Failed to create link token");
        }
        String linkToken = response
                .body()
                .getLinkToken();
        log.info("Created link token: {}", linkToken);
        return Map.of("linkToken", linkToken);
    }

    @PostMapping("/exchange-public-token")
    public Map<String, String> exchangePublicToken(@RequestBody Map<String, String> body)
            throws IOException {
        String publicToken = body.get("public_token");

        log.info("Exchanging public token: {}", publicToken);
        ItemPublicTokenExchangeRequest itemPublicTokenExchangeRequest = new
                ItemPublicTokenExchangeRequest()
                .publicToken(publicToken);

        Response<ItemPublicTokenExchangeResponse> response = plaidApi
                .itemPublicTokenExchange(itemPublicTokenExchangeRequest)
                .execute();

        log.info("Exchange public token response: {}", response.body());
        String accessToken = response.body().getAccessToken();
        log.info("Access token: {}", accessToken);


        return Map.of("accessToken", accessToken);
    }

}
