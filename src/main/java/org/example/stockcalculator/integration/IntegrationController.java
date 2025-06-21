package org.example.stockcalculator.integration;

import static org.example.stockcalculator.auth.utils.AuthUtils.currentUserId;

import java.time.LocalDateTime;
import java.util.List;

import org.example.stockcalculator.entity.IntegrationPlatform;
import org.example.stockcalculator.entity.UserIntegrationSecret;
import org.example.stockcalculator.repository.IntegrationSecretRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
public class IntegrationController {

    private final IntegrationSecretRepository integrationSecretRepository;

    @GetMapping
    public ResponseEntity<?> getIntegrations() {
        Long userId = currentUserId();
        List<UserIntegrationSecret> integrationSecrets = integrationSecretRepository.findByUserAccountId(userId);
        List<IntegrationSecretResponse> responseList = integrationSecrets.stream()
                .map(secret ->
                        new IntegrationSecretResponse(secret.getId(), secret.getPlatform(), secret.getLastChangedAt()))
                .toList();

        return ResponseEntity.ok(responseList);
    }

    record IntegrationSecretResponse(Long id, IntegrationPlatform platform, LocalDateTime lastChangedAt) {

    }

}
