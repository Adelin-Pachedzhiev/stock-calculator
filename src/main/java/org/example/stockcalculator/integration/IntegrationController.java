package org.example.stockcalculator.integration;

import static org.example.stockcalculator.auth.utils.AuthUtils.currentUserId;

import java.util.List;

import org.example.stockcalculator.entity.PlatformIntegration;
import org.example.stockcalculator.integration.dto.PlatformIntegrationResponse;
import org.example.stockcalculator.integration.repository.PlatformIntegrationJpaRepository;
import org.example.stockcalculator.transaction.repository.StockTransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    private final PlatformIntegrationJpaRepository integrationRepository;
    private final StockTransactionManager stockTransactionManager;
    private final StockTransactionRepository stockTransactionRepository;

    @GetMapping
    public ResponseEntity<?> getIntegrations() {
        Long userId = currentUserId();
        List<PlatformIntegration> integrationSecrets = integrationRepository.findByUserAccountId(userId);
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

    @DeleteMapping("/{integrationId}")
    @Transactional
    public ResponseEntity<?> deleteIntegration(@PathVariable Long integrationId) {
        stockTransactionRepository.deleteByPlatformIntegrationId(integrationId);
        integrationRepository.deleteById(integrationId);

        return ResponseEntity.ok(integrationId);
    }
}
