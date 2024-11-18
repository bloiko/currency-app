package com.currency.app.service;

import com.currency.app.AbstractBaseIT;
import com.currency.app.client.dto.ExternalRateDto;
import com.currency.app.entity.Currency;
import com.currency.app.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Transactional
class ExchangeRateUpdateServiceIT extends AbstractBaseIT {

    @Autowired
    private ExchangeRateUpdateService updateService;

    @Autowired
    private RateStorageService rateStorageService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @BeforeEach
    void setUp() {
        currencyRepository.deleteAll();
        createCurrencies();
    }

    @Test
    void updateRates_ShouldUpdateAllRates() {
        List<ExternalRateDto> mockRates = createMockExternalRates();
        when(exchangeRateClient.getCurrentExchangeRates()).thenReturn(mockRates);

        updateService.updateRates();

        verify(exchangeRateClient).getCurrentExchangeRates();

        Map<String, BigDecimal> usdRates = rateStorageService.getAllRatesForCurrency("USD");
        assertThat(usdRates).containsEntry("USDEUR", new BigDecimal("0.85"))
                            .containsEntry("USDGBP", new BigDecimal("0.73"));

        Map<String, BigDecimal> eurRates = rateStorageService.getAllRatesForCurrency("EUR");
        assertThat(eurRates).containsEntry("EURUSD", new BigDecimal("1.17"))
                            .containsEntry("EURGBP", new BigDecimal("0.86"));
    }

    private void createCurrencies() {
        List<Currency> currencies = List.of(Currency.builder().code("USD").name("US Dollar").build(), Currency.builder()
                                                                                                              .code("EUR")
                                                                                                              .name("Euro")
                                                                                                              .build(), Currency.builder()
                                                                                                                                .code("GBP")
                                                                                                                                .name("British Pound")
                                                                                                                                .build());
        currencyRepository.saveAll(currencies);
    }

    private List<ExternalRateDto> createMockExternalRates() {
        ExternalRateDto usdRates =
                new ExternalRateDto("USD", Map.of("USDEUR", new BigDecimal("0.85"), "USDGBP", new BigDecimal("0.73")));

        ExternalRateDto eurRates =
                new ExternalRateDto("EUR", Map.of("EURUSD", new BigDecimal("1.17"), "EURGBP", new BigDecimal("0.86")));

        return List.of(usdRates, eurRates);
    }
}
