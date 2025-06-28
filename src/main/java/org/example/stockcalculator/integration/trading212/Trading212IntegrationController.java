package org.example.stockcalculator.integration.trading212;

import static org.example.stockcalculator.auth.utils.AuthUtils.currentUserId;
import static org.example.stockcalculator.entity.Platform.TRADING212;

import java.util.Map;

import org.example.stockcalculator.entity.IntegrationSecret;
import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.entity.PlatformIntegration;
import org.example.stockcalculator.repository.PlatformIntegrationRepository;
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

        PlatformIntegration platformIntegration = new PlatformIntegration();
        platformIntegration.setUserAccount(new UserAccount(currentUserId()));
        platformIntegration.setSecret(new IntegrationSecret(secretTxt, platformIntegration));
        platformIntegration.setPlatform(TRADING212);

        platformIntegrationRepository.save(platformIntegration);

        return ResponseEntity.ok(Map.of("message", "Trading212 integration added successfully"));
    }

}
