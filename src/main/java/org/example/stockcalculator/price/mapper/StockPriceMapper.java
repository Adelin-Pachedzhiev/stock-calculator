package org.example.stockcalculator.price.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.example.stockcalculator.entity.StockPriceEntity;
import org.example.stockcalculator.portfolio.dto.StockPriceResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING)
public interface StockPriceMapper {

    StockPriceEntity toEntity(StockPriceResponse stockPriceResponse);
}
