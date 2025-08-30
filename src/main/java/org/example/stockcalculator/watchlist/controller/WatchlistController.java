package org.example.stockcalculator.watchlist.controller;

import java.util.List;

import org.example.stockcalculator.account.utils.AuthUtils;
import org.example.stockcalculator.stock.info.dto.StockInformationResponse;
import org.example.stockcalculator.watchlist.service.WatchlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping
    public List<StockInformationResponse> getWatchlist() {
        Long userId = AuthUtils.currentUserId();
        return watchlistService.getUserWatchlistWithStockInfo(userId);
    }

    @PostMapping("/{stockId}")
    public ResponseEntity<?> addToWatchlist(@PathVariable Long stockId) {
        Long userId = AuthUtils.currentUserId();
        watchlistService.addToWatchlist(userId, stockId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{stockId}")
    public ResponseEntity<?> removeFromWatchlist(@PathVariable Long stockId) {
        Long userId = AuthUtils.currentUserId();
        watchlistService.removeFromWatchlist(userId, stockId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{stockId}")
    public ResponseEntity<Boolean> isInWatchlist(@PathVariable Long stockId) {
        Long userId = AuthUtils.currentUserId();
        boolean exists = watchlistService.getWatchlistItem(userId, stockId).isPresent();
        return ResponseEntity.ok(exists);
    }
}
