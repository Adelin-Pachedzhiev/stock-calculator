package org.example.stockcalculator.transaction.csvupload.service;

import java.util.List;

import org.example.stockcalculator.entity.StockTransaction;
import org.springframework.web.multipart.MultipartFile;

public interface CsvTransactionParser {

    List<StockTransaction> parse(MultipartFile file);

    String getInstitution();
} 
