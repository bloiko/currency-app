package com.currency.app.client;

import com.currency.app.client.dto.ExternalRateDto;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

@Component
public class ExternalExchangeRateClientImpl implements ExternalExchangeRateClient {

    private static final List<String> AVAILABLE_CURRENCIES = Arrays.asList(
            "USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "PLN", "SEK", "NOK"
    );

    private static final Map<String, BigDecimal> BASE_RATES = Map.of(
            "EUR", BigDecimal.valueOf(0.85),
            "GBP", BigDecimal.valueOf(0.73),
            "JPY", BigDecimal.valueOf(110.0),
            "AUD", BigDecimal.valueOf(1.35),
            "CAD", BigDecimal.valueOf(1.25),
            "CHF", BigDecimal.valueOf(0.92),
            "PLN", BigDecimal.valueOf(3.75),
            "SEK", BigDecimal.valueOf(8.50),
            "NOK", BigDecimal.valueOf(8.60),
            "USD", BigDecimal.ONE
    );

    private final SecureRandom random = new SecureRandom();

    public List<ExternalRateDto> getCurrentExchangeRates() {
        List<ExternalRateDto> allRates = new ArrayList<>();

        for (String baseCurrency : AVAILABLE_CURRENCIES) {
            Map<String, BigDecimal> quotes = new HashMap<>();

            for (String targetCurrency : AVAILABLE_CURRENCIES) {
                if (!targetCurrency.equals(baseCurrency)) {
                    String quotePair = baseCurrency + targetCurrency;
                    BigDecimal baseRate = getBaseRate(baseCurrency, targetCurrency);
                    BigDecimal currentRate = generateRandomRate(baseRate);
                    quotes.put(quotePair, currentRate);
                }
            }

            allRates.add(new ExternalRateDto(baseCurrency, quotes));
        }

        return allRates;
    }

    private BigDecimal getBaseRate(String source, String target) {
        BigDecimal sourceRate = BASE_RATES.get(source);
        BigDecimal targetRate = BASE_RATES.get(target);

        return targetRate.divide(sourceRate, 6, RoundingMode.HALF_UP);
    }

    private BigDecimal generateRandomRate(BigDecimal baseRate) {
        double fluctuation = 0.98 + (random.nextDouble() * 0.04);
        return baseRate
                .multiply(BigDecimal.valueOf(fluctuation))
                .setScale(6, RoundingMode.HALF_UP);
    }
}
