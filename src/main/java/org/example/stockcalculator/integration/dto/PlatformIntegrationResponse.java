package org.example.stockcalculator.integration.dto;

import java.time.LocalDateTime;

import org.example.stockcalculator.entity.Platform;

public record PlatformIntegrationResponse(Long id, Platform platform, LocalDateTime lastChangedAt) {

    }
