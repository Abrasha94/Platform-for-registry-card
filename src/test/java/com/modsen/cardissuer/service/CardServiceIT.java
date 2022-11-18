package com.modsen.cardissuer.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.modsen.cardissuer.dto.response.CardResponseDto;
import com.modsen.cardissuer.model.*;
import com.modsen.cardissuer.repository.CardRepository;
import com.modsen.cardissuer.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@SpringBootTest()
@RunWith(SpringRunner.class)
class CardServiceIT {

    static WireMockServer wireMockServer = new WireMockServer(8082);
    static WireMockServer dynamicWireMockServer1 = new WireMockServer(options().dynamicPort());
    static WireMockServer dynamicWireMockServer2 = new WireMockServer(options().dynamicPort());
    static List<Card> cardList = new ArrayList<>();
    User user = new User();

    @Autowired
    CardService cardService;

    @MockBean
    CardRepository cardRepository;

    @MockBean
    HttpServletRequest request;

    @MockBean
    UserRepository userRepository;

    @BeforeAll
    static void setUp() {
        Card card1 = new Card();
        Card card2 = new Card();
        card1.setNumber(1L);
        card1.setPaySystem(PaySystem.VISA);
        card1.setCompany(new Company());
        card1.setBalance(BigDecimal.TEN);
        card2.setNumber(2L);
        card2.setPaySystem(PaySystem.MASTERCARD);
        card2.setCompany(new Company());
        cardList.add(card1);
        cardList.add(card2);
        wireMockServer.start();
        dynamicWireMockServer1.start();
        dynamicWireMockServer2.start();
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
        dynamicWireMockServer1.stop();
        dynamicWireMockServer2.stop();
    }

    @Test
    void findCardsByCompany() {
        doReturn("123").when(request).getHeader("keycloakUserID");
        doReturn(Optional.of(user)).when(userRepository).findByKeycloakUserId("123");
        doReturn(cardList).when(cardRepository).findByCompany(null);
        configureFor("localhost", 8082);
        stubFor(get(anyUrl()).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"balance\": 1.234,\"cardNumber\": 1111111111111111}")));
        final List<CardResponseDto> cards = cardService.findCardsByCompany(request);
        assertThat(cards.get(0).getBalance()).isEqualTo(new BigDecimal("1.234"));
        assertThat(cards.get(1).getBalance()).isEqualTo(new BigDecimal("1.234"));
    }

    @Test
    void shouldWorkFallbackMethod() {
        dynamicWireMockServer1.stubFor(get(urlEqualTo("/api/v1/balance/1111111111111111"))
                .willReturn(aResponse().withFixedDelay(10000)));
        doReturn(Optional.of(cardList.get(0))).when(cardRepository).findById(1111111111111111L);
        final Balance balance = cardService.getBalanceFromCard(1111111111111111L);
        assertThat(balance.getBalance()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void testingRibbonResponse() {
        dynamicWireMockServer1.stubFor(get(anyUrl())
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"balance\":1.234,\"cardNumber\": 1111111111111111}")));
        dynamicWireMockServer2.stubFor(get(urlEqualTo("/api/v1/balance/1111111111111111"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"balance\":4.321,\"cardNumber\": 1111111111111111}")));
        doReturn(Optional.of(cardList.get(0))).when(cardRepository).findById(1111111111111111L);
        final Balance balance = cardService.getBalanceFromCard(1111111111111111L);
        assertThat(balance.getBalance()).isEqualTo(new BigDecimal("1.234"));
    }
}