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
        Double quantity = getValueOrThrow(rowMap, getQuantityColumnName(), Double::parseDouble);
        Double price = getValueOrThrow(rowMap, getPriceColumnName(), this::convertPrice);
        String currency = getValueOrThrow(rowMap, getCurrencyColumnName(), String::valueOf);
        LocalDateTime time = parseDate(rowMap);
        String symbol = getValueOrThrow(rowMap, getSymbolColumnName(), String::valueOf);
        TransactionType type = determineTransactionType(rowMap);

        double fee = getOptionalValue(rowMap, getFeeColumnName(), Double::parseDouble).orElse(0.0);

        log.info("Transaction entity: {}", rowMap);

        StockTransaction tx = new StockTransaction();
        tx.setQuantity(quantity);
        tx.setPrice(price);
        tx.setCurrency(currency);
        tx.setTimeOfTransaction(time);
        tx.setType(type);
        tx.setFee(fee);
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        tx.setStock(stock);
        return Optional.of(tx);
    }

    protected Double convertPrice(String priceStr) {
        priceStr = priceStr.replaceAll("[^0-9.]", ""); // Remove any non-numeric characters except dot
        return Double.parseDouble(priceStr);
    }

    protected <T> Optional<T> getOptionalValue(Map<String, String> rowMap, String columnName, Function<String, T> converter) {
        try {
            T resolvedValue = getValueOrThrow(rowMap, columnName, converter);
            return Optional.of(resolvedValue);
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    protected <T> T getValueOrThrow(Map<String, String> rowMap, String columnName, Function<String, T> converter) {
        String value = rowMap.get(columnName);
        if (value == null) {
            throw new ValidationException("Column '" + columnName + "' not found in the CSV.");
        }

        if (value.isEmpty()) {
            throw new ValidationException("Column '" + columnName + "' is empty in the CSV.");
        }
        try {
            return converter.apply(value);
        }
        catch (Exception e) {
            throw new ValidationException(e);
        }
    }

    protected abstract String getQuantityColumnName();

    protected abstract String getDateColumnName();

    protected abstract String getPriceColumnName();

    protected abstract String getCurrencyColumnName();

    protected abstract String getSymbolColumnName();

    protected String getFeeColumnName() {
        return "Fee";
    }

    protected TransactionType determineTransactionType(Map<String, String> row) {
        String quantityStr = row.get(getQuantityColumnName());
        try {
            double qty = Double.parseDouble(quantityStr);
            return qty >= 0 ? TransactionType.BUY : TransactionType.SELL;
        }
        catch (Exception e) {
            throw new ValidationException("Could not determine transaction type: " + e.getMessage());
        }
    }

    public abstract String getInstitution();

    protected LocalDateTime parseDate(Map<String, String> row)
            throws ValidationException {
        String value = row.get(getDateColumnName().toLowerCase());
        try {
            return LocalDateTime.parse(value);
        }
        catch (Exception e) {
            throw new ValidationException("Could not parse date: " + e.getMessage());
        }
    }
}
