package org.example.stockcalculator.integration.dto;

import java.time.LocalDateTime;

public record PlatformIntegrationResponse(Long id, String platform, LocalDateTime lastChangedAt) {

    }
