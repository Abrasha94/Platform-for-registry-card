package com.modsen.cardissuer.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.moreThan;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
class LoadBalancerBalanceClientIntegrationTest {

    private final WireMockServer balanceService = new WireMockServer(9002);
    private final WireMockServer secondBalanceService = new WireMockServer(9003);

    @Autowired
    private BalanceClient balanceClient;


    @BeforeEach
    void setUp() throws IOException {
        balanceService.start();
        secondBalanceService.start();
        BalanceMocks.setupMockBalanceResponse(balanceService);
        BalanceMocks.setupMockBalanceResponse(secondBalanceService);
    }

    @AfterEach
    void tearDown() {
        balanceService.stop();
        secondBalanceService.stop();
    }

    @Test
    void whenUsingBalanceClient_thenUsedTwoInstances() {
        for (int i = 0; i < 10; i++) {
            balanceClient.getBalance(123L);
        }

        balanceService.verify(
                moreThan(0), getRequestedFor(urlEqualTo("/api/v1/balance/123")));
        secondBalanceService.verify(
                moreThan(0), getRequestedFor(urlEqualTo("/api/v1/balance/123")));
    }
}