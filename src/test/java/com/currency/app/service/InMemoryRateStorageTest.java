package com.currency.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryRateStorageTest {

    private InMemoryRateStorage rateStorage;

    @BeforeEach
    void setUp() {
        rateStorage = new InMemoryRateStorage();
    }

    @Test
    void getAllRatesForCurrency_WhenCurrencyExists_ShouldReturnAllRates() {
        String baseCurrency = "USD";
        rateStorage.updateRate(baseCurrency, "EUR", new BigDecimal("0.85"));
        rateStorage.updateRate(baseCurrency, "GBP", new BigDecimal("0.73"));

        Map<String, BigDecimal> rates = rateStorage.getAllRatesForCurrency(baseCurrency);

        assertThat(rates).hasSize(2)
                         .containsEntry("EUR", new BigDecimal("0.85"))
                         .containsEntry("GBP", new BigDecimal("0.73"));
    }

    @Test
    void getAllRatesForCurrency_WhenCurrencyDoesNotExist_ShouldReturnEmptyMap() {
        Map<String, BigDecimal> rates = rateStorage.getAllRatesForCurrency("USD");

        assertThat(rates).isEmpty();
    }

    @Test
    void getAllRatesForCurrency_ShouldReturnCopyOfRates() {
        String baseCurrency = "USD";
        rateStorage.updateRate(baseCurrency, "EUR", new BigDecimal("0.85"));

        Map<String, BigDecimal> rates = rateStorage.getAllRatesForCurrency(baseCurrency);
        rates.put("GBP", new BigDecimal("0.73")); // Try to modify the returned map

        Map<String, BigDecimal> newRates = rateStorage.getAllRatesForCurrency(baseCurrency);
        assertThat(newRates).hasSize(1).containsEntry("EUR", new BigDecimal("0.85")).doesNotContainKey("GBP");
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void updateRate_ShouldHandleMultipleCurrenciesConcurrently() throws InterruptedException {
        int numThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        String[] currencies = {"EUR", "GBP", "JPY", "AUD", "CAD"};

        for (int i = 0; i < numThreads; i++) {
            final String currency = currencies[i];
            executor.submit(() -> {
                try {
                    rateStorage.updateRate("USD", currency, new BigDecimal("1.0"));
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(completed).isTrue();
        Map<String, BigDecimal> rates = rateStorage.getAllRatesForCurrency("USD");
        assertThat(rates).hasSize(5);
        for (String currency : currencies) {
            assertThat(rates).containsKey(currency);
        }
    }
}
