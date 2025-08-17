package org.example.stockcalculator.integration.trading212.service;

import static org.example.stockcalculator.integration.InstitutionNameConstants.TRADING212;

import org.example.stockcalculator.integration.repository.PlatformIntegrationRepository;
import org.example.stockcalculator.integration.trading212.Trading212ApiClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class Trading212IntegrationService {

    private final PlatformIntegrationRepository platformIntegrationRepository;
    private final Trading212ApiClient trading212ApiClient;

    public void saveIntegrationAndSecret(String secret, Long userId) {
        log.info("Validating Trading212 secret for user: {}", userId);

        if (!trading212ApiClient.isTokenValid(secret)) {
            throw new IllegalArgumentException("Invalid Trading212 API token provided");
        }

        log.info("Trading212 secret is valid, saving integration for user: {}", userId);
        platformIntegrationRepository.saveIntegrationAndSecret(secret, userId, TRADING212);
    }
}
