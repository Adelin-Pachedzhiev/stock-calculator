package org.example.stockcalculator.repository;

import org.example.stockcalculator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer > {

}
