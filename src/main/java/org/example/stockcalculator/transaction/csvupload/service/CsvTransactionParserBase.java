package org.example.stockcalculator.transaction.csvupload.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.entity.TransactionType;
import org.example.stockcalculator.transaction.csvupload.ValidationException;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CsvTransactionParserBase implements CsvTransactionParser {

    @Override
    public List<StockTransaction> parse(MultipartFile file)
            throws ValidationException {
        List<StockTransaction> transactions = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = reader.readNext();
            if (header == null) {
                throw new ValidationException("CSV file is empty");
            }

            Map<String, Integer> colIndex = new HashMap<>();
            for (int i = 0; i < header.length; i++) {
                colIndex.put(header[i].trim(), i);
            }
            String[] row;
            while ((row = reader.readNext()) != null) {
                Map<String, String> rowMap = new HashMap<>();
                for (Map.Entry<String, Integer> entry : colIndex.entrySet()) {
                    rowMap.put(entry.getKey(), row[entry.getValue()]);
                }
                Optional<StockTransaction> tx = parseToTransactionEntity(rowMap);

                tx.ifPresent(transactions::add);
            }
        }
        catch (CsvValidationException | IOException e) {
            throw new ValidationException("Invalid CSV format: " + e.getMessage(), e);
        }
        return transactions;
    }

    private Optional<StockTransaction> parseToTransactionEntity(Map<String, String> rowMap) {
        Optional<Double> quantity = getValue(rowMap, getQuantityColumnName(), Double::parseDouble);
        Optional<Double> price = getValue(rowMap, getPriceColumnName(), this::convertPrice);
        Optional<String> currency = getValue(rowMap, getCurrencyColumnName(), String::valueOf);
        Optional<LocalDateTime> time = parseDate(rowMap);
        Optional<String> symbol = getValue(rowMap, getSymbolColumnName(), String::valueOf);
        Optional<TransactionType> type = determineTransactionType(rowMap);

        double fee = getValue(rowMap, getFeeColumnName(), Double::parseDouble).orElse(0.0);

        if (Stream.of(quantity, price, currency, time, symbol, type).anyMatch(Optional::isEmpty)) {
            return Optional.empty();
        }

        log.info("Transaction entity: {}", rowMap);

        StockTransaction tx = new StockTransaction();
        tx.setQuantity(quantity.get());
        tx.setPrice(price.get());
        tx.setCurrency(currency.get());
        tx.setTimeOfTransaction(time.get());
        tx.setType(type.get());
        tx.setFee(fee);
        Stock stock = new Stock();
        stock.setSymbol(symbol.get());
        tx.setStock(stock);
        return Optional.of(tx);
    }

    protected Double convertPrice(String priceStr) {
        priceStr = priceStr.replaceAll("[^0-9.]", ""); // Remove any non-numeric characters except dot
        return Double.parseDouble(priceStr);
    }

    protected <T> Optional<T> getValue(Map<String, String> rowMap, String columnName, Function<String, T> converter) {
        String value = rowMap.get(columnName);
        if (value == null) {
            throw new ValidationException("Column '" + columnName + "' not found in the CSV.");
        }

        if (!value.isEmpty()) {
            try {
                return Optional.of(converter.apply(value));
            }
            catch (Exception e) {
                throw new ValidationException(e);
            }
        }
        return Optional.empty();
    }

    protected abstract String getQuantityColumnName();

    protected abstract String getDateColumnName();

    protected abstract String getPriceColumnName();

    protected abstract String getCurrencyColumnName();

    protected abstract String getSymbolColumnName();

    protected String getFeeColumnName() {
        return "Fee";
    }

    protected Optional<TransactionType> determineTransactionType(Map<String, String> row) {
        String quantityStr = row.get(getQuantityColumnName());
        try {
            double qty = Double.parseDouble(quantityStr);
            return Optional.of(qty >= 0 ? TransactionType.BUY : TransactionType.SELL);
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    public abstract String getInstitution();

    protected Optional<LocalDateTime> parseDate(Map<String, String> row) {
        String value = row.get(getDateColumnName().toLowerCase());
        try {
            return Optional.of(LocalDateTime.parse(value));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }
} 
