package org.example.stockcalculator.account.controller;

import java.util.Map;
import java.util.Optional;

import org.example.stockcalculator.account.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<?> googleSignIn(@RequestBody Map<String, String> requestBody) {
        String idToken = requestBody.get("token");

        Optional<String> jwtToken = authService.authenticateWithGoogle(idToken);

        if (jwtToken.isEmpty()) {
            return invalidTokenResponse();
        }

        return ResponseEntity.ok(Map.of("token", jwtToken.get()));
    }

    private ResponseEntity<String> invalidTokenResponse() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide a valid token");
    }
}
