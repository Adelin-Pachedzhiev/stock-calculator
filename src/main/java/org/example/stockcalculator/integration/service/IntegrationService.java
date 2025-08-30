package org.example.stockcalculator.integration.service;

import static org.example.stockcalculator.account.utils.AuthUtils.currentUserId;

import java.util.List;

import org.example.stockcalculator.entity.PlatformIntegration;
import org.example.stockcalculator.integration.dto.PlatformIntegrationResponse;
import org.example.stockcalculator.integration.repository.IntegrationSecretRepository;
import org.example.stockcalculator.integration.repository.PlatformIntegrationJpaRepository;
import org.example.stockcalculator.transaction.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IntegrationService {

    private final PlatformIntegrationJpaRepository integrationRepository;
    private final StockTransactionRepository stockTransactionRepository;
    private final IntegrationSecretRepository integrationSecretRepository;

    public List<PlatformIntegrationResponse> getUserIntegrations() {
        Long userId = currentUserId();
        List<PlatformIntegration> integrations = integrationRepository.findByUserAccountId(userId);

        return integrations.stream()
                .map(integration ->
                        new PlatformIntegrationResponse(
                                integration.getId(),
                                integration.getPlatform(),
                                integration.getLastSyncAt(),
                                integration.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void deleteIntegration(Long integrationId) {
        stockTransactionRepository.deleteByPlatformIntegrationId(integrationId);
        integrationSecretRepository.deleteByIntegrationId(integrationId);
        integrationRepository.deleteById(integrationId);
    }
}
