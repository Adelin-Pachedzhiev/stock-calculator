package org.example.stockcalculator.integration.repository;

import org.example.stockcalculator.entity.IntegrationSecret;
import org.example.stockcalculator.entity.PlatformIntegration;
import org.example.stockcalculator.entity.UserAccount;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PlatformIntegrationRepositoryImpl implements PlatformIntegrationRepository {

    private final PlatformIntegrationJpaRepository integrationRepository;
    private final IntegrationSecretRepository integrationSecretRepository;

    public void saveIntegrationAndSecret(String secret, Long userId, String platformName ) {
        PlatformIntegration platformIntegration = new PlatformIntegration();
        platformIntegration.setUserAccount(new UserAccount(userId));
        platformIntegration.setPlatform(platformName);

        PlatformIntegration savedPlatformIntegration = integrationRepository.save(platformIntegration);

        IntegrationSecret integrationSecret = new IntegrationSecret();
        integrationSecret.setSecret(secret);//todo encrypt platformIntegration
        integrationSecret.setIntegration(savedPlatformIntegration);
        integrationSecretRepository.save(integrationSecret);
    }

    public String findAccessToken(Long userId, String platformName) {
        PlatformIntegration platformIntegration = integrationRepository.findByUserAccountIdAndPlatform(userId, platformName);
        return integrationSecretRepository.findByIntegrationId(platformIntegration.getId()).getSecret();
    }

}
