package org.example.stockcalculator.integration.trading212;

import static org.example.stockcalculator.account.utils.AuthUtils.currentUserId;

import java.util.Map;

import org.example.stockcalculator.integration.trading212.service.Trading212IntegrationService;
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

    private final Trading212IntegrationService trading212IntegrationService;

    @PostMapping
    public ResponseEntity<?> addIntegration(@RequestBody Map<String, String> requestBody) {
        String secretTxt = requestBody.get("secret");

        try {
            trading212IntegrationService.saveIntegrationAndSecret(secretTxt, currentUserId());
            return ResponseEntity.ok(Map.of("message", "Trading212 integration added successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
