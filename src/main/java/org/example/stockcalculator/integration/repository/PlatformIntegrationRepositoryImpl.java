package org.example.stockcalculator.integration.repository;

import org.example.stockcalculator.entity.IntegrationSecret;
import org.example.stockcalculator.entity.PlatformIntegration;
import org.example.stockcalculator.entity.UserAccount;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PlatformIntegrationRepositoryImpl implements PlatformIntegrationRepository {

    private final PlatformIntegrationJpaRepository repository;

    public void saveIntegrationAndSecret(String secret, Long userId, String platformName ) {
        PlatformIntegration platformIntegration = new PlatformIntegration();
        platformIntegration.setUserAccount(new UserAccount(userId));
        platformIntegration.setPlatform(platformName);
        platformIntegration.setSecret(new IntegrationSecret(secret)); //todo encrypt platformIntegration

        repository.save(platformIntegration);
    }

    public String findAccessToken(Long userId, String platformName) {
        return repository.findByUserAccountIdAndPlatform(userId, platformName).getSecret().getSecret(); //todo decrypt secret
    }

}
