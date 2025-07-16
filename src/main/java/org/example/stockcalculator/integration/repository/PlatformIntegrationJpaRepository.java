package org.example.stockcalculator.integration.repository;

import java.util.List;

import org.example.stockcalculator.entity.PlatformIntegration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformIntegrationJpaRepository extends JpaRepository<PlatformIntegration, Long> {

    PlatformIntegration findByUserAccountIdAndPlatform(Long userId, String platform);

    List<PlatformIntegration> findByUserAccountId(Long userAccountId); //todo use view to not include actual secret in the query result
}
