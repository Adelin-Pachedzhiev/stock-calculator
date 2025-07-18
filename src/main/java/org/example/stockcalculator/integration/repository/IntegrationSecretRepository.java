package org.example.stockcalculator.integration.repository;

import org.example.stockcalculator.entity.IntegrationSecret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IntegrationSecretRepository extends JpaRepository<IntegrationSecret, Long> {

    @Query("SELECT isec FROM IntegrationSecret isec JOIN PlatformIntegration pi on pi.secret.id = isec.id WHERE pi.id = :integrationId")
    IntegrationSecret findByIntegrationId(Long integrationId);


}
