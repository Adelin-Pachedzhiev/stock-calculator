package org.example.stockcalculator.init;

import org.example.stockcalculator.entity.User;
import org.example.stockcalculator.repository.UserRepository;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestUserInitializer {

    private final UserRepository userRepository;

    @PostConstruct
    public void insertDefaultStocks() {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setEmail("test@example.com");
            user.setPassword("password");
            user.setUsername("Test");

            userRepository.save(user);
        }
    }
}
