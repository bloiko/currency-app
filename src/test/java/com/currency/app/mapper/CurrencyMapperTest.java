package com.currency.app.mapper;

import com.currency.app.controller.dto.CurrencyDto;
import com.currency.app.entity.Currency;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyMapperTest {

    private final CurrencyMapper currencyMapper = Mappers.getMapper(CurrencyMapper.class);

    @Test
    void toDto_ShouldMapAllFields() {
        Currency currency = Currency.builder()
                                    .code("USD")
                                    .name("US Dollar")
                                    .build();

        CurrencyDto dto = currencyMapper.toDto(currency);

        assertThat(dto.getCode()).isEqualTo("USD");
        assertThat(dto.getName()).isEqualTo("US Dollar");
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        CurrencyDto dto = new CurrencyDto("EUR", "Euro");

        Currency entity = currencyMapper.toEntity(dto);

        assertThat(entity.getCode()).isEqualTo("EUR");
        assertThat(entity.getName()).isEqualTo("Euro");
    }

    @Test
    void toDtoList_ShouldMapAllCurrencies() {
        List<Currency> currencies = List.of(
                Currency.builder().code("USD").name("US Dollar").build(),
                Currency.builder().code("EUR").name("Euro").build()
        );

        List<CurrencyDto> dtoList = currencyMapper.toDtoList(currencies);

        assertThat(dtoList).hasSize(2);
        assertThat(dtoList.get(0).getCode()).isEqualTo("USD");
        assertThat(dtoList.get(0).getName()).isEqualTo("US Dollar");
        assertThat(dtoList.get(1).getCode()).isEqualTo("EUR");
        assertThat(dtoList.get(1).getName()).isEqualTo("Euro");
    }

    @Test
    void toDto_WhenNull_ShouldReturnNull() {
        assertThat(currencyMapper.toDto(null)).isNull();
    }

    @Test
    void toEntity_WhenNull_ShouldReturnNull() {
        assertThat(currencyMapper.toEntity(null)).isNull();
    }

    @Test
    void toDtoList_WhenNull_ShouldReturnNull() {
        assertThat(currencyMapper.toDtoList(null)).isNull();
    }
}
