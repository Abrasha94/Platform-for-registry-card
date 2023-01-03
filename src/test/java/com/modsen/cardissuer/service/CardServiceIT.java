package com.modsen.cardissuer.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.modsen.cardissuer.dto.response.CardResponseDto;
import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.PaySystem;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.repository.CardRepository;
import com.modsen.cardissuer.util.MethodsUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CardServiceIT {

    static WireMockServer wireMockServer = new WireMockServer(8082);
    static List<Card> cardList = new ArrayList<>();
    User user = new User();

    @Autowired
    CardService cardService;

    @MockBean
    CardRepository cardRepository;

    @MockBean
    MethodsUtil methodsUtil;

    @BeforeAll
    static void setUp() {
        Card card1 = new Card();
        Card card2 = new Card();
        card1.setNumber(1L);
        card1.setPaySystem(PaySystem.VISA);
        card1.setCompany(new Company());
        card2.setNumber(2L);
        card2.setPaySystem(PaySystem.MASTERCARD);
        card2.setCompany(new Company());
        cardList.add(card1);
        cardList.add(card2);
        wireMockServer.start();
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void findCardsByCompany() {
        doReturn(user).when(methodsUtil).getUserFromRequest(null);
        doReturn(cardList).when(cardRepository).findByCompany(null);
        configureFor("localhost", 8082);
        stubFor(get(anyUrl()).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"balance\": 1.234,\"cardNumber\": 1111111111111111}")));
        final List<CardResponseDto> cards = cardService.findCardsByCompany(null);
        assertEquals(new BigDecimal("1.234"), cards.get(0).getBalance());
        assertEquals(new BigDecimal("1.234"), cards.get(1).getBalance());
    }
}