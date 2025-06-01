package org.example.stockcalculator.integration.plaid;

import static org.example.stockcalculator.integration.plaid.PlaidConfigurations.Environment.PRODUCTION;

import java.util.HashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.plaid.client.ApiClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PlaidApiClientConfiguration {

    private final PlaidConfigurations config;

    @Bean
    public ApiClient plaidApiClient() {
        HashMap<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", config.clientId());
        apiKeys.put("secret", config.secret());

        ApiClient apiClient = new ApiClient(apiKeys);
        apiClient.setPlaidAdapter(config.environment().equals(PRODUCTION) ? ApiClient.Production : ApiClient.Sandbox);

        return apiClient;
    }
}
