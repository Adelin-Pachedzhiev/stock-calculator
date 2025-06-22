package org.example.stockcalculator.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.model.StockPriceResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING)
public interface StockPriceMapper {

    StockPriceEntity toEntity(StockPriceResponse stockPriceResponse);
}
