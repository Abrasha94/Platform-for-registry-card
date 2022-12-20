package com.modsen.cardissuer.client;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@ActiveProfiles("ribbon-test")
@AutoConfigureWireMock(port = 0)
@TestPropertySource("classpath:application-ribbon-test.yml")
class LoadBalancerBalanceClientIntegrationTest {

    @RegisterExtension
    protected static WireMockExtension BalanceService = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(9002))
            .build();

    @Autowired
    private BalanceClient balanceClient;


    @BeforeEach
    void setUp() throws IOException {
        BalanceMocks.setupMockBalanceResponse(BalanceService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getBalance() {

        for (int i = 0; i < 10; i++) {
            balanceClient.getBalance(123L);
        }

        BalanceService.verify(
                moreThan(0), getRequestedFor(urlEqualTo("/api/v1/balance/123")));
//        secondMockBalanceService.verify(
//                moreThan(0), getRequestedFor(urlEqualTo("/123")));
    }
}