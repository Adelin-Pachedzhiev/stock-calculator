package org.example.stockcalculator.repository;

import org.example.stockcalculator.entity.IntegrationPlatform;
import org.example.stockcalculator.entity.UserIntegrationSecret;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationSecretRepository extends JpaRepository<UserIntegrationSecret, Long> {

    UserIntegrationSecret findByUserAccountIdAndPlatform(Long userId, IntegrationPlatform platform);
}
