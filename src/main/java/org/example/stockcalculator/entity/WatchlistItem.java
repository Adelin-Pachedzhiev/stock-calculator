package org.example.stockcalculator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "stock_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;
} 
