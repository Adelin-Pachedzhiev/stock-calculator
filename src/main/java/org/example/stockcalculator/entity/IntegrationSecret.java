package org.example.stockcalculator.entity;

import org.example.stockcalculator.entity.encrypt.IntegrationSecretConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

    @Convert(converter = IntegrationSecretConverter.class)
    private String secret;

    @OneToOne
    @JoinColumn(name = "integration_id")
    private PlatformIntegration integration;

    public IntegrationSecret(String secret) {
        this.secret = secret;
    }
}
