package com.currency.app.service;

import com.currency.app.entity.ExchangeRate;
import com.currency.app.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DatabaseRateStorage implements RateStorage {

    private final ExchangeRateRepository exchangeRateRepository;

    private final CurrencyService currencyService;

    @Override
    public void updateRate(String baseCurrency, String targetCurrency, BigDecimal rate) {
        ExchangeRate exchangeRate = ExchangeRate.builder()
                                                .baseCurrency(currencyService.getOrCreateCurrency(baseCurrency))
                                                .targetCurrency(currencyService.getOrCreateCurrency(targetCurrency))
                                                .rate(rate)
                                                .lastUpdated(LocalDateTime.now())
                                                .build();

        exchangeRateRepository.save(exchangeRate);
    }

    @Override
    public Map<String, BigDecimal> getAllRatesForCurrency(String baseCurrency) {
        return exchangeRateRepository.findLatestRatesForBaseCurrency(baseCurrency)
                                     .stream()
                                     .collect(Collectors.toMap(rate -> rate.getTargetCurrency()
                                                                           .getCode(), ExchangeRate::getRate));
    }
}
