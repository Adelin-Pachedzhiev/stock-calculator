package org.example.stockcalculator.transaction.csvupload;

import static org.example.stockcalculator.account.utils.AuthUtils.currentUserId;

import java.util.List;
import java.util.Optional;

import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.StockTransaction;
import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.stock.repository.StockRepository;
import org.example.stockcalculator.transaction.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CsvTransactionUploadServiceImpl implements CsvTransactionUploadService {

    private final CsvTransactionParserStrategyResolver resolver;
    private final StockRepository stockRepository;
    private final StockTransactionRepository transactionRepository;

    @Override
    public void handleUpload(MultipartFile file, String institution) {
        CsvTransactionParserService parser = resolver.resolve(institution);
        if (parser == null) {
            throw new ValidationException("Unsupported institution: " + institution);
        }

        List<StockTransaction> transactions = parser.parse(file);
        Long userId = currentUserId();

        List<StockTransaction> readyToSaveTransactions = transactions.stream().peek(tx -> {
            tx.setUser(new UserAccount(userId));

            String symbol = tx.getStock().getSymbol();
            Optional<Stock> stockOpt = stockRepository.findBySymbol(symbol);
            Stock stock = stockOpt.orElseThrow(() -> new ValidationException("Unsupported stock with symbol: " + symbol));
            tx.setStock(stock);
        }).toList();

        transactionRepository.saveAll(readyToSaveTransactions);
    }
}
