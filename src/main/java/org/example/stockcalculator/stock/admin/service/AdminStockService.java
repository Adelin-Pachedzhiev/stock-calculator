package org.example.stockcalculator.stock.admin.service;

import java.util.List;
import java.util.Optional;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminStockService {

    private final StockRepository stockRepository;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Optional<Stock> getStockById(Long id) {
        return stockRepository.findById(id);
    }

    @Transactional
    public Stock updateStock(Long id, Stock updatedStock) {
        return stockRepository.findById(id)
                .map(existingStock -> {
                    existingStock.setSymbol(updatedStock.getSymbol());
                    existingStock.setName(updatedStock.getName());
                    existingStock.setDescription(updatedStock.getDescription());
                    return stockRepository.save(existingStock);
                })
                .orElseThrow(() -> new RuntimeException("Stock not found with id: " + id));
    }

    @Transactional
    public void deleteStock(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new RuntimeException("Stock not found with id: " + id);
        }
        stockRepository.deleteById(id);
    }

    @Transactional
    public Stock createStock(Stock stock) {
        stock.setId(null);
        return stockRepository.save(stock);
    }
}
