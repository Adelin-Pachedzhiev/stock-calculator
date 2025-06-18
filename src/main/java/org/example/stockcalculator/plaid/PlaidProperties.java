package org.example.stockcalculator.plaid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.NonNull;

@ConfigurationProperties(prefix = "stock.plaid-api")
@Validated
public record PlaidProperties(
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
