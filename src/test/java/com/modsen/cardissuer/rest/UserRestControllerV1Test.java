package com.modsen.cardissuer.rest;

import com.modsen.cardissuer.dto.response.CardResponseDto;
import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.PaySystem;
import com.modsen.cardissuer.model.Type;
import com.modsen.cardissuer.service.CardService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;


@WebMvcTest(UserRestControllerV1.class)
class UserRestControllerV1Test {

    @MockBean
    CardService cardService;

    @Autowired
    MockMvc mockMvc;

    Card card = new Card();

    @BeforeEach
    void setUp() {
        card.setNumber(1L);
        card.setBalance(BigDecimal.TEN);
        card.setStatus("test");
        card.setType(Type.PERSONAL);
        card.setPaySystem(PaySystem.VISA);
        card.setCompany(new Company());

        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void whenGetAllCard_thenReturnRightCard() {
        when(cardService.findCardsByUser(Mockito.any(HttpServletRequest.class))).thenReturn(List.of(CardResponseDto.fromCard(card)));

        given()
                .when()
                .get("/api/v1/user/cards")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1))
                .body("[0].number", equalTo(1))
                .body("[0].status", equalTo("test"));
    }
}