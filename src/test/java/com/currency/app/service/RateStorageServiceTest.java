package com.currency.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateStorageServiceTest {

    @Mock
    private DatabaseRateStorage databaseRateStorage;

    @Mock
    private InMemoryRateStorage inMemoryRateStorage;

    private RateStorageService rateStorageService;

    @BeforeEach
    void setUp() {
        rateStorageService = new RateStorageService(databaseRateStorage, inMemoryRateStorage);
    }

    @Test
    void updateRate_ShouldUpdateBothStorages() {
        String baseCurrency = "USD";
        String targetCurrency = "EUR";
        BigDecimal rate = new BigDecimal("0.85");

        rateStorageService.updateRate(baseCurrency, targetCurrency, rate);

        verify(inMemoryRateStorage).updateRate(baseCurrency, targetCurrency, rate);
        verify(databaseRateStorage).updateRate(baseCurrency, targetCurrency, rate);
    }

    @Test
    void getAllRatesForCurrency_ShouldPreferInMemoryStorage() {
        String baseCurrency = "USD";
        Map<String, BigDecimal> expectedRates = Map.of(
                "EUR", new BigDecimal("0.85"),
                "GBP", new BigDecimal("0.73")
        );
        when(inMemoryRateStorage.getAllRatesForCurrency(baseCurrency)).thenReturn(expectedRates);

        Map<String, BigDecimal> result = rateStorageService.getAllRatesForCurrency(baseCurrency);

        assertThat(result).isEqualTo(expectedRates);
        verify(inMemoryRateStorage).getAllRatesForCurrency(baseCurrency);
        verifyNoInteractions(databaseRateStorage);
    }

    @Test
    void getAllRatesForCurrency_WhenInMemoryEmpty_ShouldUseDatabaseStorage() {
        String baseCurrency = "USD";
        Map<String, BigDecimal> expectedRates = Map.of(
                "EUR", new BigDecimal("0.85")
        );
        when(inMemoryRateStorage.getAllRatesForCurrency(baseCurrency)).thenReturn(Map.of());
        when(databaseRateStorage.getAllRatesForCurrency(baseCurrency)).thenReturn(expectedRates);

        Map<String, BigDecimal> result = rateStorageService.getAllRatesForCurrency(baseCurrency);

        assertThat(result).isEqualTo(expectedRates);
        verify(inMemoryRateStorage).getAllRatesForCurrency(baseCurrency);
        verify(databaseRateStorage).getAllRatesForCurrency(baseCurrency);
    }

    @Test
    void getAllRatesForCurrency_WhenAllStoragesEmpty_ShouldReturnEmptyMap() {
        String baseCurrency = "USD";
        when(inMemoryRateStorage.getAllRatesForCurrency(baseCurrency)).thenReturn(Map.of());
        when(databaseRateStorage.getAllRatesForCurrency(baseCurrency)).thenReturn(Map.of());

        Map<String, BigDecimal> result = rateStorageService.getAllRatesForCurrency(baseCurrency);

        assertThat(result).isEmpty();
        verify(inMemoryRateStorage).getAllRatesForCurrency(baseCurrency);
        verify(databaseRateStorage).getAllRatesForCurrency(baseCurrency);
    }
}
