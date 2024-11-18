package com.currency.app;

import com.currency.app.client.ExternalExchangeRateClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class AbstractBaseIT {

    @MockBean
    protected ExternalExchangeRateClient exchangeRateClient;
}
