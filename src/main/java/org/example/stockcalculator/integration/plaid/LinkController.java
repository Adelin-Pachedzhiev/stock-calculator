package org.example.stockcalculator.integration.plaid;

import java.io.IOException;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plaid.client.model.ItemGetRequest;
import com.plaid.client.model.ItemGetResponse;
import com.plaid.client.model.ItemPublicTokenExchangeRequest;
import com.plaid.client.model.ItemPublicTokenExchangeResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

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
