package org.example.stockcalculator.transaction.csvupload.controller;

import java.util.HashMap;
import java.util.Map;

import org.example.stockcalculator.transaction.csvupload.ValidationException;
import org.example.stockcalculator.transaction.csvupload.service.CsvTransactionUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions/csv")
@RequiredArgsConstructor
public class CsvTransactionUploadController {
    private final CsvTransactionUploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadTransactions(
            @Valid @RequestParam("file") MultipartFile file,
            @Valid @RequestParam("institution") String institution
    ) {
        uploadService.handleUpload(file, institution);
        return ResponseEntity.ok("Transactions uploaded successfully.");
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
} 
