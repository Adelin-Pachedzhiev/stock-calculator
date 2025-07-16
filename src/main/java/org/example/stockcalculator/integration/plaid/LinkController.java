package org.example.stockcalculator.integration.plaid;

import static com.plaid.client.model.CountryCode.CA;
import static com.plaid.client.model.CountryCode.US;
import static org.example.stockcalculator.account.utils.AuthUtils.currentUserId;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.example.stockcalculator.integration.repository.PlatformIntegrationRepository;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plaid.client.model.ItemGetRequest;
import com.plaid.client.model.ItemGetResponse;
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
    private final PlatformIntegrationRepository platformIntegrationRepository;

    @PostMapping("/link-token")
    public ResponseEntity<?> createLinkToken()
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

        if (!response.isSuccessful()) {
            log.error("Failed to create link token: {}", response.errorBody().string());

            return ResponseEntity.internalServerError().body("Failed to create link token");
        }
        String linkToken = response
                .body()
                .getLinkToken();

        return ResponseEntity.ok(Map.of("linkToken", linkToken));
    }

    @PostMapping("/exchange-public-token")
    public ResponseEntity<?> exchangePublicToken(@RequestBody Map<String, String> body)
            throws IOException {
        String publicToken = body.get("public_token");

        String accessToken = exchangePublicForAccessToken(publicToken);

        String institutionName = fetchInstitutionName(accessToken);

        platformIntegrationRepository.saveIntegrationAndSecret(accessToken, currentUserId(), institutionName);

        return ResponseEntity.ok("Access token saved successfully");
    }

    private String exchangePublicForAccessToken(String publicToken)
            throws IOException {
        log.info("Exchanging public token: {}", publicToken);
        ItemPublicTokenExchangeRequest itemPublicTokenExchangeRequest = new
                ItemPublicTokenExchangeRequest()
                .publicToken(publicToken);

        Response<ItemPublicTokenExchangeResponse> response = plaidApi
                .itemPublicTokenExchange(itemPublicTokenExchangeRequest)
                .execute();

        log.info("Exchange public token response: {}", response.body());
        return response.body().getAccessToken();
    }

    @Nullable
    private String fetchInstitutionName(String accessToken)
            throws IOException {
        ItemGetRequest itemGetRequest = new ItemGetRequest();
        itemGetRequest.accessToken(accessToken);
        Response<ItemGetResponse> item = plaidApi.itemGet(itemGetRequest).execute();
        return item.body().getItem().getInstitutionName();
    }

}
