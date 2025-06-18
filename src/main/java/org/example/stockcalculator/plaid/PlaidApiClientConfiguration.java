package org.example.stockcalculator.plaid;

import static org.example.stockcalculator.plaid.PlaidProperties.Environment.PRODUCTION;

import java.util.HashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.plaid.client.ApiClient;
import com.plaid.client.request.PlaidApi;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PlaidApiClientConfiguration {

    private final PlaidProperties config;

    @Bean
    public ApiClient plaidApiClient() {
        HashMap<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", config.clientId());
        apiKeys.put("secret", config.secret());

        ApiClient apiClient = new ApiClient(apiKeys);
        apiClient.setPlaidAdapter(config.environment().equals(PRODUCTION) ? ApiClient.Production : ApiClient.Sandbox);

        return apiClient;
    }

    @Bean
    public PlaidApi plaidApi() {
        return plaidApiClient().createService(PlaidApi.class);
    }
}
