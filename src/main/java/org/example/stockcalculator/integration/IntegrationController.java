package org.example.stockcalculator.integration;

import static org.example.stockcalculator.auth.utils.AuthUtils.currentUserId;

import java.util.List;

import org.example.stockcalculator.entity.PlatformIntegration;
import org.example.stockcalculator.integration.dto.PlatformIntegrationResponse;
import org.example.stockcalculator.repository.PlatformIntegrationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
public class IntegrationController {

    private final PlatformIntegrationRepository integrationSecretRepository;
    private final StockTransactionManager stockTransactionManager;

    @GetMapping
    public ResponseEntity<?> getIntegrations() {
        Long userId = currentUserId();
        List<PlatformIntegration> integrationSecrets = integrationSecretRepository.findByUserAccountId(userId);
        List<PlatformIntegrationResponse> responseList = integrationSecrets.stream()
                .map(secret ->
                        new PlatformIntegrationResponse(secret.getId(), secret.getPlatform(), secret.getLastChangedAt()))
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/{integrationId}/sync")
    public ResponseEntity<?> syncTransaction(@PathVariable Long integrationId) {
        stockTransactionManager.syncTransactionsToDbForIntegration(integrationId);

        return ResponseEntity.ok(integrationId);

    }




}
