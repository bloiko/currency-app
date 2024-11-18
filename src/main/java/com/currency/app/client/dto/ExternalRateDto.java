package com.currency.app.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalRateDto {

    private String source;

    private Map<String, BigDecimal> quotes;
}
