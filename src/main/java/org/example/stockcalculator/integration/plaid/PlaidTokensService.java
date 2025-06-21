package org.example.stockcalculator.integration.plaid;

import static org.example.stockcalculator.entity.IntegrationPlatform.PLAID;

import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.entity.UserIntegrationSecret;
import org.example.stockcalculator.repository.IntegrationSecretRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaidTokensService {

    private final IntegrationSecretRepository repository;

    public void saveAccessToken(String accessToken, Long userId) {
        UserIntegrationSecret secret = new UserIntegrationSecret();
        secret.setUserAccount(new UserAccount(userId));
        secret.setPlatform(PLAID);
        secret.setSecret(accessToken); //todo encrypt secret

        repository.save(secret);
    }

    public String findAccessToken(Long userId) {
        return repository.findByUserAccountIdAndPlatform(userId, PLAID).getSecret(); //todo decrypt secret
    }

}
