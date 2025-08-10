package org.example.stockcalculator.transaction.csvupload.service;

import static java.time.ZoneOffset.UTC;
import static org.example.stockcalculator.entity.TransactionType.BUY;
import static org.example.stockcalculator.entity.TransactionType.SELL;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

import org.example.stockcalculator.entity.TransactionType;
import org.example.stockcalculator.transaction.csvupload.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class RevolutCsvTransactionParser extends CsvTransactionParserBase {

    @Override
    protected String getQuantityColumnName() {
        return "Quantity";
    }

    @Override
    protected String getDateColumnName() {
        return "Date";
    }

    @Override
    protected String getPriceColumnName() {
        return "Price per share";
    }

    @Override
    protected String getCurrencyColumnName() {
        return "Currency";
    }

    @Override
    protected String getSymbolColumnName() {
        return "Ticker";
    }

    @Override
    protected TransactionType determineTransactionType(Map<String, String> row) {
        String type = row.get("Type");

        if (type == null) {
            throw new ValidationException("No 'Type' column found in the CSV.");
        }

        if (type.contains("BUY")) {
            return BUY;
        }
        else if (type.contains("SELL")) {
            return SELL;
        }

        throw new ValidationException("Unknown transaction type '" + type + "'.");
    }

    @Override
    public String getInstitution() {
        return "revolut";
    }

    @Override
    protected LocalDateTime parseDate(Map<String, String> row) {
        String dateColValue = row.get(getDateColumnName());
        if (dateColValue == null || dateColValue.isEmpty()) {
            throw new ValidationException("Date column '" + getDateColumnName() + "' is missing or empty in the CSV.");
        }

        Instant instant = Instant.parse(dateColValue);
        return LocalDateTime.ofInstant(instant, UTC);
    }
} 
