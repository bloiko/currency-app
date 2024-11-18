package com.currency.app.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryRateStorage implements RateStorage {

    private final ConcurrentHashMap<String, Map<String, BigDecimal>> rateMap = new ConcurrentHashMap<>();

    @Override
    public void updateRate(String baseCurrency, String targetCurrency, BigDecimal rate) {
        rateMap.computeIfAbsent(baseCurrency, k -> new ConcurrentHashMap<>()).put(targetCurrency, rate);
    }

    @Override
    public Map<String, BigDecimal> getAllRatesForCurrency(String baseCurrency) {
        return new ConcurrentHashMap<>(rateMap.getOrDefault(baseCurrency, Map.of()));
    }
}
