package org.example.stockcalculator.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class IntegrationSecret {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String secret;

    @OneToOne(mappedBy = "secret")
    @JsonBackReference
    private PlatformIntegration integration;

    public IntegrationSecret(String secret, PlatformIntegration integration) {
        this.secret = secret;
        this.integration = integration;
    }
}
