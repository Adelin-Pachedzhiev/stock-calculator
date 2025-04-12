package org.example.stockcalculator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Stock stock;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    private LocalDateTime timestamp;
    private Double price;
    private Double fee;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
}
