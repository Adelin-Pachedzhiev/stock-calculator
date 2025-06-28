package org.example.stockcalculator.integration.plaid;

import static org.example.stockcalculator.entity.Platform.PLAID;

import org.example.stockcalculator.entity.IntegrationSecret;
import org.example.stockcalculator.entity.PlatformIntegration;
import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.repository.PlatformIntegrationRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaidTokensService {

    private final PlatformIntegrationRepository repository;

    public void saveAccessToken(String accessToken, Long userId) {
        PlatformIntegration platformIntegration = new PlatformIntegration();
        platformIntegration.setUserAccount(new UserAccount(userId));
        platformIntegration.setPlatform(PLAID);
        platformIntegration.setSecret(new IntegrationSecret(accessToken, platformIntegration)); //todo encrypt platformIntegration

        repository.save(platformIntegration);
    }

    public String findAccessToken(Long userId) {
        return repository.findByUserAccountIdAndPlatform(userId, PLAID).getSecret().getSecret(); //todo decrypt secret
    }

}
