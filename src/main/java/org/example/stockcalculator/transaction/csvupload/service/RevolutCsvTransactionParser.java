package org.example.stockcalculator.transaction.csvupload.service;

import static java.time.ZoneOffset.UTC;
import static org.example.stockcalculator.entity.TransactionType.BUY;
import static org.example.stockcalculator.entity.TransactionType.SELL;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.example.stockcalculator.entity.TransactionType;
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
    protected Optional<TransactionType> determineTransactionType(Map<String, String> row) {
        String type = row.get("Type");

        if (type == null) {
            return Optional.empty();
        }

        if (type.contains("BUY")) {
            return Optional.of(BUY);
        }
        else if (type.contains("SELL")) {
            return Optional.of(SELL);
        }

        return Optional.empty();
    }

    @Override
    public String getInstitution() {
        return "revolut";
    }

    @Override
    protected Optional<LocalDateTime> parseDate(Map<String, String> row) {
        String dateColValue = row.get(getDateColumnName());
        if (dateColValue == null || dateColValue.isEmpty()) {
            return Optional.empty();
        }

        Instant instant = Instant.parse(dateColValue);
        return Optional.of(LocalDateTime.ofInstant(instant, UTC));
    }
} 
