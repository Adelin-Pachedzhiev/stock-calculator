package org.example.stockcalculator.transaction.csvupload.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvTransactionParserStrategyResolver {
    private final List<CsvTransactionParser> parserServices;

    public CsvTransactionParser resolve(String institution) {
        return parserServices.stream()
                .filter(s -> s.getInstitution().equalsIgnoreCase(institution))
                .findFirst()
                .orElse(null);
    }
} 
