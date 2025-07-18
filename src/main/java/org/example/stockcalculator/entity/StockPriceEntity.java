package org.example.stockcalculator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_price")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Stock stock;

    private Double change;
    private Double changePercent;
    private Double highPrice;
    private Double lowPrice;
    private Double openPrice;
    private Double previousClosePrice;

    private LocalDateTime timestamp;
    private Double price;
}
