package org.example.stockcalculator.entity;

import static org.example.stockcalculator.entity.UserRole.*;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String givenName;
    private String familyName;
    @Column(unique = true)
    private String email;
    private String pictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;


    public UserAccount(Long id){
        this.setId(id);
    }
}
