package org.example.stockcalculator.account.service;

import java.util.Optional;

import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.account.repository.UserAccountRepository;
import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userRepository;

    public Optional<UserAccount> getUserByEmail(String email){
        UserAccount user = userRepository.findByEmailIgnoreCase(email);

        return Optional.ofNullable(user);
    }

    public UserAccount createUserFromPayload(GoogleIdToken.Payload payload){
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(payload.getEmail());
        userAccount.setGivenName(payload.get("given_name").toString());
        userAccount.setFamilyName(payload.get("family_name").toString());
        userAccount.setPictureUrl(payload.get("picture").toString());

        return userRepository.save(userAccount);
    }
}
