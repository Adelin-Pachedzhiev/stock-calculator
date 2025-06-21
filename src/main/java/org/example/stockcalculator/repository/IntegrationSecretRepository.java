package org.example.stockcalculator.repository;

import java.util.List;

import org.example.stockcalculator.entity.IntegrationPlatform;
import org.example.stockcalculator.entity.UserIntegrationSecret;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationSecretRepository extends JpaRepository<UserIntegrationSecret, Long> {

    UserIntegrationSecret findByUserAccountIdAndPlatform(Long userId, IntegrationPlatform platform);

    List<UserIntegrationSecret> findByUserAccountId(Long userAccountId); //todo use view to not include actual secret in the query result
}
