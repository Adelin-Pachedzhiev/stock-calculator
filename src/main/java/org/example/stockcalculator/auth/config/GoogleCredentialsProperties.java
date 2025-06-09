package org.example.stockcalculator.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.google")
public record GoogleCredentialsProperties(
        String clientId,
        String clientSecret) {

}
