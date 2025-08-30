package org.example.stockcalculator.integration.controller;

import java.util.List;

import org.example.stockcalculator.integration.StockTransactionManager;
import org.example.stockcalculator.integration.dto.PlatformIntegrationResponse;
import org.example.stockcalculator.integration.service.IntegrationService;
import org.springframework.http.ResponseEntity;
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

    private final IntegrationService integrationService;
    private final StockTransactionManager stockTransactionManager;

    @GetMapping
    public ResponseEntity<?> getIntegrations() {
        List<PlatformIntegrationResponse> integrations = integrationService.getUserIntegrations();
        return ResponseEntity.ok(integrations);
    }

    @PostMapping("/{integrationId}/sync")
    public ResponseEntity<?> syncTransaction(@PathVariable Long integrationId) {
        stockTransactionManager.syncTransactionsToDbForIntegration(integrationId);
        return ResponseEntity.ok(integrationId);
    }

    @DeleteMapping("/{integrationId}")
    public ResponseEntity<?> deleteIntegration(@PathVariable Long integrationId) {
        integrationService.deleteIntegration(integrationId);
        return ResponseEntity.ok(integrationId);
    }
}
