package com.currency.app.service;

import com.currency.app.entity.Currency;
import com.currency.app.exception.CurrencyAlreadyExistsException;
import com.currency.app.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    private Currency usdCurrency;

    private Currency eurCurrency;

    @BeforeEach
    void setUp() {
        usdCurrency = Currency.builder().code("USD").name("US Dollar").build();

        eurCurrency = Currency.builder().code("EUR").name("Euro").build();
    }

    @Test
    void getAllCurrencies_ShouldReturnAllCurrencies() {
        when(currencyRepository.findAll()).thenReturn(List.of(usdCurrency, eurCurrency));

        List<Currency> result = currencyService.getAllCurrencies();

        assertThat(result).hasSize(2).containsExactly(usdCurrency, eurCurrency);
        verify(currencyRepository).findAll();
    }

    @Test
    void addCurrency_WhenCurrencyExists_ThrowsException() {
        Currency currency = Currency.builder().code("GBP").name("British Pound").build();
        when(currencyRepository.existsByCode("GBP")).thenReturn(true);

        CurrencyAlreadyExistsException exception =
                assertThrows(CurrencyAlreadyExistsException.class, () -> currencyService.addCurrency(currency));

        assertThat(exception.getMessage()).isEqualTo("Currency already exists: GBP");
        verify(currencyRepository).existsByCode("GBP");
        verify(currencyRepository, never()).save(any());
    }

    @Test
    void getOrCreateCurrency_WhenExists_ShouldReturnExisting() {
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usdCurrency));

        Currency result = currencyService.getOrCreateCurrency("USD");

        assertThat(result).isEqualTo(usdCurrency);
        verify(currencyRepository).findByCode("USD");
        verify(currencyRepository, never()).save(any());
    }

    @Test
    void getOrCreateCurrency_WhenNotExists_ShouldCreateNew() {
        Currency newCurrency = Currency.builder().code("GBP").name("GBP").build();
        when(currencyRepository.findByCode("GBP")).thenReturn(Optional.empty());
        when(currencyRepository.save(any(Currency.class))).thenReturn(newCurrency);

        Currency result = currencyService.getOrCreateCurrency("GBP");

        assertThat(result).isEqualTo(newCurrency);
        verify(currencyRepository).findByCode("GBP");
        verify(currencyRepository).save(any());
    }
}
