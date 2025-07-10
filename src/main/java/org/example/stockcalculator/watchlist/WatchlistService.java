package org.example.stockcalculator.watchlist;

import lombok.RequiredArgsConstructor;
import org.example.stockcalculator.entity.Stock;
import org.example.stockcalculator.entity.UserAccount;
import org.example.stockcalculator.entity.WatchlistItem;
import org.example.stockcalculator.repository.StockRepository;
import org.example.stockcalculator.repository.UserAccountRepository;
import org.example.stockcalculator.repository.WatchlistItemRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WatchlistService {
    private final WatchlistItemRepository watchlistItemRepository;
    private final UserAccountRepository userAccountRepository;
    private final StockRepository stockRepository;

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
} 
