package org.example.stockcalculator.entity;

import static jakarta.persistence.GenerationType.*;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String symbol;
    private String name;
    @Column(length = 1000)
    private String description;

    public Stock(Long id){
        this.setId(id);
    }
}
