package org.example.stockcalculator.transaction.csvupload;

import java.util.List;

import org.example.stockcalculator.entity.StockTransaction;
import org.springframework.web.multipart.MultipartFile;

public interface CsvTransactionParserService {
    List<StockTransaction> parse(MultipartFile file);
    String getInstitution();
} 
