package com.currency.app.mapper;

import com.currency.app.controller.dto.CurrencyDto;
import com.currency.app.entity.Currency;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    CurrencyDto toDto(Currency currency);

    Currency toEntity(CurrencyDto dto);

    List<CurrencyDto> toDtoList(List<Currency> currencies);
}
