package com.currency.app.controller;

import com.currency.app.controller.dto.CurrencyDto;
import com.currency.app.controller.dto.ExchangeRateDto;
import com.currency.app.entity.Currency;
import com.currency.app.exception.CurrencyAlreadyExistsException;
import com.currency.app.mapper.CurrencyMapper;
import com.currency.app.service.CurrencyService;
import com.currency.app.service.RateStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing currency exchange operations.
 * Provides endpoints for retrieving currency information and exchange rates,
 * as well as adding new currencies to the system.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/currencies")
@Tag(name = "Currency Controller", description = "Currency exchange rate operations API")
public class CurrencyController {

    private final CurrencyService currencyService;

    private final RateStorageService rateStorageService;

    private final CurrencyMapper currencyMapper;

    /**
     * Retrieves all currencies currently supported by the system.
     *
     * @return List of available currencies
     */
    @GetMapping
    @Operation(summary = "Get all supported currencies")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved list of currencies"), @ApiResponse(responseCode = "500", description = "Internal server error occurred")})
    public List<CurrencyDto> getAllCurrencies() {
        List<Currency> currencies = currencyService.getAllCurrencies();
        return currencyMapper.toDtoList(currencies);
    }

    /**
     * Retrieves current exchange rates for the specified currency code.
     *
     * @param code the three-letter currency code (e.g., "USD", "EUR")
     * @return Exchange rates for the specified currency
     */
    @GetMapping("/{code}/rates")
    @Operation(summary = "Get exchange rates for currency")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved exchange rates"), @ApiResponse(responseCode = "404", description = "Currency not found"), @ApiResponse(responseCode = "500", description = "Internal server error occurred")})
    public ExchangeRateDto getExchangeRates(
            @Parameter(description = "Currency code (e.g., USD)", required = true) @PathVariable String code) {
        Map<String, BigDecimal> rates = rateStorageService.getAllRatesForCurrency(code);
        return new ExchangeRateDto(code, rates);
    }


    /**
     * Adds a new currency to the system for exchange rate tracking.
     *
     * @param currencyDto the currency data transfer object
     * @return Created currency
     * @throws CurrencyAlreadyExistsException if the currency already exists
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add new currency")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Currency successfully created"), @ApiResponse(responseCode = "400", description = "Currency already exists"), @ApiResponse(responseCode = "500", description = "Internal server error occurred")})
    public CurrencyDto addCurrency(
            @Parameter(description = "Currency details", required = true) @RequestBody CurrencyDto currencyDto) {
        Currency currency = currencyMapper.toEntity(currencyDto);
        Currency savedCurrency = currencyService.addCurrency(currency);
        return currencyMapper.toDto(savedCurrency);
    }
}
