package org.example.stockcalculator.entity;

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


    public UserAccount(Long id){
        this.setId(id);
    }
}
