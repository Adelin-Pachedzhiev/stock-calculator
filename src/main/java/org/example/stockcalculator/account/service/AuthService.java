package org.example.stockcalculator.account.service;

import java.util.Optional;

import org.example.stockcalculator.entity.UserAccount;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final GoogleTokenVerifierService tokenVerifierService;
    private final UserAccountService userAccountService;
    private final JwtService jwtService;

    public Optional<String> authenticateWithGoogle(String idToken) {
        if (idToken == null || idToken.isEmpty()) {
            return Optional.empty();
        }

        var payloadOptional = tokenVerifierService.verify(idToken);
        if (payloadOptional.isEmpty()) {
            return Optional.empty();
        }

        GoogleIdToken.Payload payload = payloadOptional.get();
        Optional<UserAccount> userByEmail = userAccountService.getUserByEmail(payload.getEmail());

        UserAccount userAccount = userByEmail
                .orElseGet(() -> userAccountService.createUserFromPayload(payload));

        String jwtToken = jwtService.generateTokenForAccount(userAccount);
        log.info("Google Token created for user {}", userAccount.getEmail());

        return Optional.of(jwtToken);
    }
}
