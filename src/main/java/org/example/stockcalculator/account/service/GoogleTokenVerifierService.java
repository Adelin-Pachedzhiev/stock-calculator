package org.example.stockcalculator.account.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;

    public Optional<GoogleIdToken.Payload> verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            return Optional.ofNullable(idToken)
                    .map(GoogleIdToken::getPayload);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return Optional.empty();
        }
    }

}
