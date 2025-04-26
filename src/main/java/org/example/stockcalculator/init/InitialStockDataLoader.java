package org.example.stockcalculator.init;

import java.util.List;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.repository.StockRepository;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitialStockDataLoader {

    private final StockRepository stockRepository;

    @PostConstruct
    public void insertDefaultStocks() {
        if (stockRepository.count() == 0) {
            List<Stock> defaultStocks = List.of(
                    createStock("AAPL", "Apple Inc.", "Technology company known for iPhone, Mac, and more."),
                    createStock("GOOGL", "Alphabet Inc.", "Parent company of Google."),
                    createStock("AMZN", "Amazon.com Inc.", "E-commerce and cloud computing giant."),
                    createStock("MSFT", "Microsoft Corporation", "Known for Windows, Office, Azure."),
                    createStock("TSLA", "Tesla Inc.", "Electric vehicles and clean energy.")
            );
            stockRepository.saveAll(defaultStocks);
        }
    }
    private Stock createStock(String symbol, String name, String description) {
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setName(name);
        stock.setDescription(description);
        return stock;
    }
}
