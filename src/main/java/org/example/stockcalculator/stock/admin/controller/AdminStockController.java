package org.example.stockcalculator.stock.admin.controller;

import java.util.List;
import java.util.Map;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.stock.admin.service.AdminStockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/stock")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminStockController {

    private final AdminStockService adminStockService;

    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = adminStockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        return adminStockService.getStockById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createStock(@RequestBody Stock stock) {
        try {
            Stock createdStock = adminStockService.createStock(stock);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStock);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorMessage(e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorMessage("Failed to create stock: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestBody Stock stock) {
        try {
            Stock updatedStock = adminStockService.updateStock(id, stock);
            return ResponseEntity.ok(updatedStock);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorMessage(e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorMessage("Failed to create stock: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        try {
            adminStockService.deleteStock(id);
            return ResponseEntity.noContent().build();
        }
        catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Map<String, String> createErrorMessage(String message) {
        return Map.of("message", message);
    }
}
