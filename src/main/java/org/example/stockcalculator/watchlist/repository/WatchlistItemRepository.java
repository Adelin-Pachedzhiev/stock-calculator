package org.example.stockcalculator.watchlist.repository;

import java.util.List;
import java.util.Optional;

import org.example.stockcalculator.entity.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {

    List<WatchlistItem> findByUserId(Long userId);

    Optional<WatchlistItem> findByUserIdAndStockId(Long userId, Long stockId);

    void deleteByUserIdAndStockId(Long userId, Long stockId);
} 
