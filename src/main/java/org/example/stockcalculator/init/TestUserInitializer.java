package org.example.stockcalculator.init;

import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.repository.UserRepository;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestUserInitializer {

    private final UserRepository userRepository;

    @PostConstruct
    public void insertDefaultUser() {
        if (userRepository.count() == 0) {
            UserAccount user = new UserAccount();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setGivenName("Test");
            user.setFamilyName("TestSurname");

            userRepository.save(user);
        }
    }
}
