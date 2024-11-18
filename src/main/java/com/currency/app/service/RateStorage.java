package com.currency.app.service;

import java.math.BigDecimal;
import java.util.Map;

public interface RateStorage {

    void updateRate(String baseCurrency, String targetCurrency, BigDecimal rate);

    Map<String, BigDecimal> getAllRatesForCurrency(String baseCurrency);
}
