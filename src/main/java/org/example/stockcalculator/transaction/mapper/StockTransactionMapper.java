package org.example.stockcalculator.transaction.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.example.stockcalculator.transaction.dto.TransactionPayload;
import org.example.stockcalculator.entity.StockTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface StockTransactionMapper {

    @Mapping(source = "stockId", target = "stock.id")
    StockTransaction toEntity(TransactionPayload dto);
}
