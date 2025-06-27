package org.example.stockcalculator.integration.trading212;

import static org.example.stockcalculator.auth.utils.AuthUtils.currentUserId;
import static org.example.stockcalculator.entity.IntegrationPlatform.TRADING212;

import java.util.Map;

import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.entity.UserIntegrationSecret;
import org.example.stockcalculator.repository.IntegrationSecretRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/integration/trading212")
@RequiredArgsConstructor
public class Trading212IntegrationController {

    private final IntegrationSecretRepository integrationSecretRepository;

    @PostMapping
    public ResponseEntity<?> addIntegration(@RequestBody Map<String, String> requestBody) {
        String secretTxt = requestBody.get("secret");

        UserIntegrationSecret integrationSecret = new UserIntegrationSecret();
        integrationSecret.setUserAccount(new UserAccount(currentUserId()));
        integrationSecret.setSecret(secretTxt);
        integrationSecret.setPlatform(TRADING212);

        integrationSecretRepository.save(integrationSecret);

        return ResponseEntity.ok(Map.of("message", "Trading212 integration added successfully"));
    }

}
