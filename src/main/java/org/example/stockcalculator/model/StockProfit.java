package org.example.stockcalculator.model;

public record StockProfit(
        double profit,
        double profitPercentage) {

    public StockProfit() {
        this(0, 0);
    }

    public StockProfit sum(StockProfit other){
        double profit = this.profit() + other.profit();
        double profitPercentage = this.profitPercentage() + other.profitPercentage();
        //todo check if this is correct
        //todo percentage should be averaged
        return new StockProfit(profit, profitPercentage);
    }
}
