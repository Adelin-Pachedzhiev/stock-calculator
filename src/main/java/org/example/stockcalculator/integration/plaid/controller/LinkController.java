package org.example.stockcalculator.integration.plaid.controller;

import java.io.IOException;
import java.util.Map;

import org.example.stockcalculator.integration.plaid.service.PlaidLinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/plaid")
@RequiredArgsConstructor
public class LinkController {

    private final PlaidLinkService plaidLinkService;

    @PostMapping("/link-token")
    public ResponseEntity<?> createLinkToken()
            throws IOException {
        try {
            String linkToken = plaidLinkService.createLinkToken();
            return ResponseEntity.ok(Map.of("linkToken", linkToken));
        }
        catch (RuntimeException e) {
            log.error("Failed to create link token", e);
            return ResponseEntity.internalServerError().body("Failed to create link token");
        }
    }

    @PostMapping("/exchange-public-token")
    public ResponseEntity<?> exchangePublicToken(@RequestBody Map<String, String> body)
            throws IOException {
        String publicToken = body.get("public_token");

        try {
            plaidLinkService.exchangePublicToken(publicToken);
        }
        catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Failed to exchange public token");
        }

        return ResponseEntity.ok("Access token saved successfully");
    }
}
