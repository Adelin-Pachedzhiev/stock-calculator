package org.example.stockcalculator.transaction.csvupload.service;

import org.springframework.web.multipart.MultipartFile;

public interface CsvTransactionUploadService {

    void handleUpload(MultipartFile file, String institution);
} 
