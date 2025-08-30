package org.example.stockcalculator.stock.admin.service;

import java.util.List;
import java.util.Optional;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.price.StockPricePersister;
import org.example.stockcalculator.price.client.StockPriceApiClient;
import org.example.stockcalculator.price.repository.StockPriceRepository;
import org.example.stockcalculator.stock.repository.StockRepository;
import org.example.stockcalculator.transaction.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminStockService {

    private final StockRepository stockRepository;
    private final StockPriceApiClient stockPriceApiClient;
    private final StockPricePersister stockPricePersister;
    private final StockPriceRepository stockPriceRepository;
    private final StockTransactionRepository stockTransactionRepository;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Optional<Stock> getStockById(Long id) {
        return stockRepository.findById(id);
    }

    @Transactional
    public Stock updateStock(Long id, Stock updatedStock) {
        checkIfStockSymbolIsValid(updatedStock.getSymbol());
        checkIfStockSymbolIsDoesNotExist(updatedStock.getSymbol());

        Stock stock = stockRepository.findById(id)
                .map(existingStock -> {
                    existingStock.setSymbol(updatedStock.getSymbol());
                    existingStock.setName(updatedStock.getName());
                    existingStock.setDescription(updatedStock.getDescription());
                    return stockRepository.save(existingStock);
                })
                .orElseThrow(() -> new RuntimeException("Stock not found with id: " + id));

        stockPricePersister.getPriceAndSaveToDb(stock);

        return stock;
    }

    @Transactional
    public Stock createStock(Stock stock) {
        checkIfStockSymbolIsValid(stock.getSymbol());
        checkIfStockSymbolIsDoesNotExist(stock.getSymbol());

        stock.setId(null);
        Stock savedStock = stockRepository.save(stock);

        stockPricePersister.getPriceAndSaveToDb(savedStock);

        return savedStock;
    }

    @Transactional
    public void deleteStock(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new RuntimeException("Stock not found with id: " + id);
        }

        stockTransactionRepository.deleteByStockId(id);
        stockPriceRepository.deleteByStockId(id);

        stockRepository.deleteById(id);
    }

    private void checkIfStockSymbolIsValid(String symbol) {
        if (!stockPriceApiClient.isSymbolSupported(symbol)) {
            throw new IllegalArgumentException("Stock symbol " + symbol + " is not a valid company symbol.");
        }
    }

    private void checkIfStockSymbolIsDoesNotExist(String symbol) {
        if (stockRepository.findBySymbol(symbol).isPresent()) {
            throw new IllegalArgumentException("Stock with symbol " + symbol + " already exists.");
        }
    }
}
