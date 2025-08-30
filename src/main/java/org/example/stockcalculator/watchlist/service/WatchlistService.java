package org.example.stockcalculator.watchlist.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.stockcalculator.account.repository.UserAccountRepository;
import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.entity.WatchlistItem;
import org.example.stockcalculator.stock.info.dto.StockInformationResponse;
import org.example.stockcalculator.stock.info.service.StockInformationService;
import org.example.stockcalculator.stock.repository.StockRepository;
import org.example.stockcalculator.watchlist.repository.WatchlistItemRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistItemRepository watchlistItemRepository;
    private final UserAccountRepository userAccountRepository;
    private final StockRepository stockRepository;
    private final StockInformationService stockInformationService;

    public void addToWatchlist(Long userId, Long stockId) {
        UserAccount user = userAccountRepository.findById(userId).orElseThrow();
        Stock stock = stockRepository.findById(stockId).orElseThrow();
        watchlistItemRepository.save(new WatchlistItem(null, user, stock));
    }

    public void removeFromWatchlist(Long userId, Long stockId) {
        watchlistItemRepository.deleteByUserIdAndStockId(userId, stockId);
    }

    public Optional<WatchlistItem> getWatchlistItem(Long userId, Long stockId) {
        return watchlistItemRepository.findByUserIdAndStockId(userId, stockId);
    }

    public List<StockInformationResponse> getUserWatchlistWithStockInfo(Long userId) {
        return watchlistItemRepository.findByUserId(userId).stream()
                .map(item -> stockInformationService.getStockInformation(item.getStock().getSymbol()))
                .collect(Collectors.toList());
    }
}
