package org.example.stockcalculator.integration.repository;

import org.example.stockcalculator.entity.IntegrationSecret;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationSecretRepository extends JpaRepository<IntegrationSecret, Long> {

    IntegrationSecret findByIntegrationId(Long integrationId);
}
