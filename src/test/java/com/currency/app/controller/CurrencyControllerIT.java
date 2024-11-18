package com.currency.app.controller;

import com.currency.app.AbstractBaseIT;
import com.currency.app.controller.dto.CurrencyDto;
import com.currency.app.entity.Currency;
import com.currency.app.repository.CurrencyRepository;
import com.currency.app.service.RateStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CurrencyControllerIT extends AbstractBaseIT {

    private static final String BASE_URL = "/api/v1/currencies";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private RateStorageService rateStorageService;

    @BeforeEach
    void setup() {
        currencyRepository.deleteAll();
    }

    @Test
    void getAllCurrencies_WhenEmpty_ReturnsEmptyList() throws Exception {
        ResultActions response = mockMvc.perform(get(BASE_URL));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andDo(print());
    }

    @Test
    void getAllCurrencies_WithData_ReturnsList() throws Exception {
        createCurrency("USD", "US Dollar");
        createCurrency("EUR", "Euro");

        ResultActions response = mockMvc.perform(get(BASE_URL));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code", is("USD")))
                .andExpect(jsonPath("$[0].name", is("US Dollar")))
                .andExpect(jsonPath("$[1].code", is("EUR")))
                .andExpect(jsonPath("$[1].name", is("Euro")))
                .andDo(print());
    }

    @Test
    void getExchangeRates_WhenCurrencyExists_ReturnsRates() throws Exception {
        Currency usd = createCurrency("USD", "US Dollar");
        updateRates(usd);

        ResultActions response = mockMvc.perform(get(BASE_URL + "/{code}/rates", usd.getCode())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.baseCurrency", is("USD")))
                .andExpect(jsonPath("$.rates.EUR", is(0.85)))
                .andDo(print());
    }

    @Test
    void addCurrency_WithValidData_ReturnsCreated() throws Exception {
        CurrencyDto currencyDto = new CurrencyDto("USD", "US Dollar");

        ResultActions response = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currencyDto)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("USD")))
                .andExpect(jsonPath("$.name", is("US Dollar")))
                .andDo(print());
    }

    private Currency createCurrency(String code, String name) {
        Currency currency = Currency.builder()
                                    .code(code)
                                    .name(name)
                                    .build();
        return currencyRepository.save(currency);
    }

    private void updateRates(Currency baseCurrency) {
        Map<String, BigDecimal> rates = Map.of(
                "EUR", new BigDecimal("0.85"),
                "GBP", new BigDecimal("0.73")
        );
        rateStorageService.updateRate(baseCurrency.getCode(), "EUR", rates.get("EUR"));
        rateStorageService.updateRate(baseCurrency.getCode(), "GBP", rates.get("GBP"));
    }
}
