package org.example.stockcalculator.account.controller;

import java.util.Map;
import java.util.Optional;

import org.example.stockcalculator.account.service.GoogleTokenVerifierService;
import org.example.stockcalculator.account.service.JwtService;
import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.account.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GoogleTokenVerifierService tokenVerifierService;
    private final UserAccountService userAccountService;
    private final JwtService jwtService;

    @PostMapping("/google")
    public ResponseEntity<?> googleSignIn(@RequestBody Map<String, String> requestBody) {
        String idToken = requestBody.get("token");
        if (idToken == null || idToken.isEmpty()) {
            return invalidTokenResponse();
        }

        var payloadOptional = tokenVerifierService.verify(idToken);
        if (payloadOptional.isEmpty()) {
            return invalidTokenResponse();
        }

        GoogleIdToken.Payload payload = payloadOptional.get();
        Optional<UserAccount> userByEmail = userAccountService.getUserByEmail(payload.getEmail());

        UserAccount userAccount = userByEmail
                .orElseGet(() -> userAccountService.createUserFromPayload(payload));

        String jwtToken = jwtService.generateTokenForAccount(userAccount);
        log.info("Google Token created for user {}", userAccount.getEmail());

        return ResponseEntity.ok(Map.of("token", jwtToken));
    }

    private ResponseEntity<String> invalidTokenResponse() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide a valid token");
    }
}
