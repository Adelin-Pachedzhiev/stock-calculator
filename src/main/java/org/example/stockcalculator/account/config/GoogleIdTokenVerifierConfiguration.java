package org.example.stockcalculator.account.config;

import static java.util.Collections.*;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleIdTokenVerifierConfiguration {

    private final GoogleCredentialsProperties googleCredentials;

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(singletonList(googleCredentials.clientId()))
                .build();
    }

}
