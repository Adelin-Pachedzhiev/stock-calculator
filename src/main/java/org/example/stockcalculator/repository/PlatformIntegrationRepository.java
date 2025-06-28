package org.example.stockcalculator.repository;

import java.util.List;

import org.example.stockcalculator.entity.Platform;
import org.example.stockcalculator.entity.PlatformIntegration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformIntegrationRepository extends JpaRepository<PlatformIntegration, Long> {

    PlatformIntegration findByUserAccountIdAndPlatform(Long userId, Platform platform);

    List<PlatformIntegration> findByUserAccountId(Long userAccountId); //todo use view to not include actual secret in the query result
}
