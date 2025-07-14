package org.example.stockcalculator.integration.repository;

public interface PlatformIntegrationRepository {

    void saveIntegrationAndSecret(String secret, Long userId, String platformName);

    String findAccessToken(Long userId, String platformName);
}
