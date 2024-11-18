package com.currency.app.service;

import com.currency.app.entity.Currency;
import com.currency.app.entity.ExchangeRate;
import com.currency.app.repository.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseRateStorageTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private DatabaseRateStorage databaseRateStorage;

    @Captor
    private ArgumentCaptor<ExchangeRate> exchangeRateCaptor;

    private Currency usdCurrency;

    private Currency eurCurrency;

    private ExchangeRate exchangeRate;

    @BeforeEach
    void setUp() {
        usdCurrency = Currency.builder()
                              .code("USD")
                              .name("US Dollar")
                              .build();

        eurCurrency = Currency.builder()
                              .code("EUR")
                              .name("Euro")
                              .build();

        exchangeRate = ExchangeRate.builder()
                                   .baseCurrency(usdCurrency)
                                   .targetCurrency(eurCurrency)
                                   .rate(new BigDecimal("0.85"))
                                   .lastUpdated(LocalDateTime.now())
                                   .build();
    }

    @Test
    void updateRate_ShouldSaveNewExchangeRate() {
        String baseCurrency = "USD";
        String targetCurrency = "EUR";
        BigDecimal rate = new BigDecimal("0.85");

        when(currencyService.getOrCreateCurrency(baseCurrency)).thenReturn(usdCurrency);
        when(currencyService.getOrCreateCurrency(targetCurrency)).thenReturn(eurCurrency);
        when(exchangeRateRepository.save(any(ExchangeRate.class))).thenReturn(exchangeRate);

        databaseRateStorage.updateRate(baseCurrency, targetCurrency, rate);

        verify(exchangeRateRepository).save(exchangeRateCaptor.capture());
        ExchangeRate savedRate = exchangeRateCaptor.getValue();

        assertThat(savedRate.getBaseCurrency()).isEqualTo(usdCurrency);
        assertThat(savedRate.getTargetCurrency()).isEqualTo(eurCurrency);
        assertThat(savedRate.getRate()).isEqualTo(rate);
        assertThat(savedRate.getLastUpdated()).isNotNull();
    }

    @Test
    void getAllRatesForCurrency_ShouldReturnAllRates() {
        String baseCurrency = "USD";
        Currency gbpCurrency = Currency.builder()
                                       .code("GBP")
                                       .name("British Pound")
                                       .build();

        ExchangeRate gbpRate = ExchangeRate.builder()
                                           .baseCurrency(usdCurrency)
                                           .targetCurrency(gbpCurrency)
                                           .rate(new BigDecimal("0.73"))
                                           .lastUpdated(LocalDateTime.now())
                                           .build();

        List<ExchangeRate> rates = List.of(exchangeRate, gbpRate);
        when(exchangeRateRepository.findLatestRatesForBaseCurrency(baseCurrency))
                .thenReturn(rates);

        Map<String, BigDecimal> result = databaseRateStorage.getAllRatesForCurrency(baseCurrency);

        assertThat(result)
                .hasSize(2)
                .containsEntry("EUR", new BigDecimal("0.85"))
                .containsEntry("GBP", new BigDecimal("0.73"));
        verify(exchangeRateRepository).findLatestRatesForBaseCurrency(baseCurrency);
    }

    @Test
    void getAllRatesForCurrency_WhenNoRates_ShouldReturnEmptyMap() {
        String baseCurrency = "USD";
        when(exchangeRateRepository.findLatestRatesForBaseCurrency(baseCurrency))
                .thenReturn(List.of());

        Map<String, BigDecimal> result = databaseRateStorage.getAllRatesForCurrency(baseCurrency);

        assertThat(result).isEmpty();
        verify(exchangeRateRepository).findLatestRatesForBaseCurrency(baseCurrency);
    }

    @Test
    void updateRate_ShouldHandleNewCurrencies() {
        String baseCurrency = "USD";
        String targetCurrency = "GBP";
        BigDecimal rate = new BigDecimal("0.73");

        Currency gbpCurrency = Currency.builder()
                                       .code("GBP")
                                       .name("British Pound")
                                       .build();

        when(currencyService.getOrCreateCurrency(baseCurrency)).thenReturn(usdCurrency);
        when(currencyService.getOrCreateCurrency(targetCurrency)).thenReturn(gbpCurrency);
        when(exchangeRateRepository.save(any(ExchangeRate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        databaseRateStorage.updateRate(baseCurrency, targetCurrency, rate);

        verify(currencyService).getOrCreateCurrency(baseCurrency);
        verify(currencyService).getOrCreateCurrency(targetCurrency);
        verify(exchangeRateRepository).save(exchangeRateCaptor.capture());

        ExchangeRate savedRate = exchangeRateCaptor.getValue();
        assertThat(savedRate.getBaseCurrency()).isEqualTo(usdCurrency);
        assertThat(savedRate.getTargetCurrency()).isEqualTo(gbpCurrency);
        assertThat(savedRate.getRate()).isEqualTo(rate);
    }

    @Test
    void updateRate_ShouldUpdateLastUpdatedTimestamp() {
        LocalDateTime beforeUpdate = LocalDateTime.now();
        String baseCurrency = "USD";
        String targetCurrency = "EUR";
        BigDecimal rate = new BigDecimal("0.85");

        when(currencyService.getOrCreateCurrency(baseCurrency)).thenReturn(usdCurrency);
        when(currencyService.getOrCreateCurrency(targetCurrency)).thenReturn(eurCurrency);
        when(exchangeRateRepository.save(any(ExchangeRate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        databaseRateStorage.updateRate(baseCurrency, targetCurrency, rate);

        verify(exchangeRateRepository).save(exchangeRateCaptor.capture());
        ExchangeRate savedRate = exchangeRateCaptor.getValue();

        assertThat(savedRate.getLastUpdated())
                .isNotNull()
                .isAfterOrEqualTo(beforeUpdate);
    }
}
