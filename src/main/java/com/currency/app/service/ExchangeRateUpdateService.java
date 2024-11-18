package com.currency.app.service;

import com.currency.app.client.ExternalExchangeRateClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExchangeRateUpdateService {

    private final ExternalExchangeRateClient exchangeRateClient;

    private final RateStorageService rateStorageService;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updateRates() {
        exchangeRateClient.getCurrentExchangeRates().forEach(exchange -> {
            exchange.getQuotes()
                    .forEach((targetCurrency, rate) -> rateStorageService.updateRate(exchange.getSource(), targetCurrency, rate));
        });
    }
}
