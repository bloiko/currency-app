package com.currency.app.client;

import com.currency.app.client.dto.ExternalRateDto;

import java.util.List;

public interface ExternalExchangeRateClient {

    List<ExternalRateDto> getCurrentExchangeRates();
}
