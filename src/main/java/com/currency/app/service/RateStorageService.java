package com.currency.app.service;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class RateStorageService implements RateStorage {

    private final List<RateStorage> rateStorages;

    public RateStorageService(DatabaseRateStorage databaseRateStorage, InMemoryRateStorage inMemoryRateStorage) {
        this.rateStorages = List.of(databaseRateStorage, inMemoryRateStorage);
    }

    public void updateRate(String baseCurrency, String targetCurrency, BigDecimal rate) {
        rateStorages.forEach(storage -> storage.updateRate(baseCurrency, targetCurrency, rate));
    }

    public Map<String, BigDecimal> getAllRatesForCurrency(String baseCurrency) {
        return rateStorages.stream()
                           .filter(storage -> storage instanceof InMemoryRateStorage)
                           .findFirst()
                           .map(storage -> storage.getAllRatesForCurrency(baseCurrency))
                           .filter(map -> !CollectionUtils.isEmpty(map))
                           .orElseGet(() -> getFromFirstAvailableStorage(baseCurrency));
    }

    private Map<String, BigDecimal> getFromFirstAvailableStorage(String baseCurrency) {
        return rateStorages.stream()
                           .filter(storage -> !(storage instanceof InMemoryRateStorage))
                           .map(storage -> storage.getAllRatesForCurrency(baseCurrency))
                           .filter(map -> !CollectionUtils.isEmpty(map))
                           .findFirst()
                           .orElse(Map.of());
    }
}
