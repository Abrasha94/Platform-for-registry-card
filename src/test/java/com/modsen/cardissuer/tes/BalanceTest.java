package com.modsen.cardissuer.tes;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.modsen.cardissuer.client.BalanceClient;
import com.modsen.cardissuer.client.BalanceMocks;
import com.modsen.cardissuer.model.Balance;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WireMockConfig.class})
public class BalanceTest {

    @Autowired
    private WireMockServer mockServer;

    @Autowired
    private BalanceClient balanceClient;

    @BeforeEach
    void setUp() throws IOException {
//        BalanceMocks.setupMockBalanceResponse(mockServer);
    }

    @Test
    public void whenGetBooks_thenBooksShouldBeReturned() {
        final ResponseEntity<Balance> balance = balanceClient.getBalance(123L);

        assertThat(balance).isNotNull();
        assertThat(balance.getBody().getBalance()).isEqualTo(BigDecimal.valueOf(123.456));
    }
}
