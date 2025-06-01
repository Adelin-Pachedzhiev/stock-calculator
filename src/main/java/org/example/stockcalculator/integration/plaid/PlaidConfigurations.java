package org.example.stockcalculator.integration.plaid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.NonNull;

@ConfigurationProperties(prefix = "stock.plaid-api")
@Validated
public record PlaidConfigurations(
        @NonNull
        String clientId,
        @NonNull
        String secret,
        @NonNull
        Environment environment) {

    enum Environment {
        SANDBOX,
        PRODUCTION
    }
}
