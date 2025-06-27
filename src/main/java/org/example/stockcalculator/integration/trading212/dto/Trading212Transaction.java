package org.example.stockcalculator.integration.trading212.dto;

import java.time.LocalDateTime;
import java.util.List;

public record Trading212Transaction(
        Long id,
        String ticker,
        Double orderedValue,
        Double filledValue,
        LocalDateTime dateModified,
        Double fillPrice,
        Double orderedQuantity,
        List<TransactionTax> taxes
) {

}
