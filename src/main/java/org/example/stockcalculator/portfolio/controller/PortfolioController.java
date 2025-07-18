package org.example.stockcalculator.portfolio.controller;

import org.example.stockcalculator.portfolio.service.PortfolioService;
import org.example.stockcalculator.portfolio.dto.PortfolioOverview;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.stockcalculator.account.utils.AuthUtils;

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
