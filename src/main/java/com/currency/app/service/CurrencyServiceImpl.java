package com.currency.app.service;

import com.currency.app.entity.Currency;
import com.currency.app.exception.CurrencyAlreadyExistsException;
import com.currency.app.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    @Override
    @Transactional
    public Currency addCurrency(Currency currency) {
        if (currencyRepository.existsByCode(currency.getCode())) {
            throw new CurrencyAlreadyExistsException("Currency already exists: " + currency.getCode());
        }

        return currencyRepository.save(currency);
    }

    public Currency getOrCreateCurrency(String code) {
        return currencyRepository.findByCode(code).orElseGet(() -> {
            Currency newCurrency = Currency.builder().code(code).name(code).build();
            return currencyRepository.save(newCurrency);
        });
    }
}
