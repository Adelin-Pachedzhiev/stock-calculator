package org.example.stockcalculator.watchlist.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.example.stockcalculator.account.utils.AuthUtils;
import org.example.stockcalculator.stock.info.StockInformationService;
import org.example.stockcalculator.stock.info.dto.StockInformationResponse;
import org.example.stockcalculator.watchlist.repository.WatchlistItemRepository;
import org.example.stockcalculator.watchlist.service.WatchlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistItemRepository watchlistItemRepository;
    private final StockInformationService stockInformationService;
    private final WatchlistService watchlistService;

    @GetMapping
    public List<StockInformationResponse> getWatchlist() {
        Long userId = AuthUtils.currentUserId();
        return watchlistItemRepository.findByUserId(userId).stream()
                .map(item -> stockInformationService.getStockInformation(item.getStock().getSymbol()))
                .collect(Collectors.toList());
    }

    @PostMapping("/{stockId}")
    public ResponseEntity<?> addToWatchlist(@PathVariable Long stockId) {
        Long userId = AuthUtils.currentUserId();
        watchlistService.addToWatchlist(userId, stockId);
        return ResponseEntity.ok().build();
    }

    @Transactional
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
