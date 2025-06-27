package org.example.stockcalculator.transaction.dto;

import java.time.LocalDateTime;

import org.example.stockcalculator.entity.TransactionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record TransactionPayload(

        @NotNull(message = "Stock ID cannot be null")
        Long stockId,

        @NotNull(message = "You need to specify a quantity")
        @Positive(message = "Quantity must be positive")
        Double quantity,

        @NotNull(message = "You need to provide a price per unit")
        @Positive(message = "Price must be positive")
        Double price,

        @PositiveOrZero
        @NotNull
        Double fee,

        @NotNull
        TransactionType type,

        @NotNull
        LocalDateTime timeOfTransaction,

        @NotNull(message = "Currency cannot be null")
        String currency
) {
}
