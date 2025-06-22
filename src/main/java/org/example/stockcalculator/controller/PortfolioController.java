package org.example.stockcalculator.controller;

import org.example.stockcalculator.model.PortfolioOverview;
import org.example.stockcalculator.service.PortfolioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.stockcalculator.auth.utils.AuthUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/overview")
    public PortfolioOverview getPortfolioOverview() {
        return portfolioService.getPortfolioOverview(AuthUtils.currentUserId());
    }
} 