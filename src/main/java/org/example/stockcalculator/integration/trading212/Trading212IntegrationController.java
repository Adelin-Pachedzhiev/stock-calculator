package org.example.stockcalculator.integration.trading212;

import static org.example.stockcalculator.account.utils.AuthUtils.currentUserId;
import static org.example.stockcalculator.integration.InstitutionNameConstants.TRADING212;

import java.util.Map;

import org.example.stockcalculator.integration.repository.PlatformIntegrationRepository;
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

    private final PlatformIntegrationRepository platformIntegrationRepository;

    @PostMapping
    public ResponseEntity<?> addIntegration(@RequestBody Map<String, String> requestBody) {
        String secretTxt = requestBody.get("secret");

        platformIntegrationRepository.saveIntegrationAndSecret(secretTxt, currentUserId(), TRADING212);

        return ResponseEntity.ok(Map.of("message", "Trading212 integration added successfully"));
    }

}
