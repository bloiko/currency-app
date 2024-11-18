package com.currency.app.repository;

import com.currency.app.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    /**
     * Finds all latest rates for a base currency.
     */
    @Query("SELECT er FROM ExchangeRate er " +
            "WHERE er.baseCurrency.code = :baseCode " +
            "AND er.lastUpdated = (" +
            "    SELECT MAX(er2.lastUpdated) " +
            "    FROM ExchangeRate er2 " +
            "    WHERE er2.baseCurrency = er.baseCurrency " +
            "    AND er2.targetCurrency = er.targetCurrency" +
            ")")
    List<ExchangeRate> findLatestRatesForBaseCurrency(@Param("baseCode") String baseCode);
}
