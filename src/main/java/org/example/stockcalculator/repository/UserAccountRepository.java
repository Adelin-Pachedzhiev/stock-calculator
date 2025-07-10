package org.example.stockcalculator.repository;

import org.example.stockcalculator.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long > {

    UserAccount findByEmailIgnoreCase(String email);
}
