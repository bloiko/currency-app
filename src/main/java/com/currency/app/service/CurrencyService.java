package com.currency.app.service;

import com.currency.app.entity.Currency;

import java.util.List;

public interface CurrencyService {

    List<Currency> getAllCurrencies();

    Currency addCurrency(Currency currency);

    Currency getOrCreateCurrency(String code);
}
