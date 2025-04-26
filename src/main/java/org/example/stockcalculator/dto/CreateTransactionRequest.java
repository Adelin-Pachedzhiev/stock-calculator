package org.example.stockcalculator.dto;

import java.time.LocalDateTime;

import org.example.stockcalculator.entity.TransactionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTransactionRequest(
        @NotNull(message = "User ID cannot be null")
        Long userId,

        @NotNull(message = "Stock ID cannot be null")
        Long stockId,

        @NotNull(message = "You need to specify a quantity")
        @Positive(message = "Quantity must be positive")
        Integer quantity,

        @NotNull(message = "You need to provide a price per unit")
        Double pricePerUnit,

        Double fee,

        @NotNull
        TransactionType type,

        @NotNull
        LocalDateTime timestamp
) {
}
