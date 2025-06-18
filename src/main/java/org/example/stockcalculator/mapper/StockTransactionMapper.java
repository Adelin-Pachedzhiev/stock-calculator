package org.example.stockcalculator.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.example.stockcalculator.dto.CreateTransactionRequest;
import org.example.stockcalculator.entity.StockTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface StockTransactionMapper {

    @Mapping(source = "stockId", target = "stock.id")
    StockTransaction toEntity(CreateTransactionRequest dto);
}
