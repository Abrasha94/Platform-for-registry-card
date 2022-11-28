package com.modsen.cardissuer.rest;

import com.modsen.cardissuer.dto.request.AccountantRegisterUserDto;
import com.modsen.cardissuer.dto.request.CardOrderDto;
import com.modsen.cardissuer.dto.request.ChangeUserPermissionDto;
import com.modsen.cardissuer.dto.request.ChangeUsersInCardDto;
import com.modsen.cardissuer.dto.response.CardResponseDto;
import com.modsen.cardissuer.model.*;
import com.modsen.cardissuer.service.CardService;
import com.modsen.cardissuer.service.UserService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@WebMvcTest(AccountantRestControllerV1.class)
class AccountantRestControllerV1Test {

    @MockBean
    CardService cardService;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    Card card = new Card();
    User user = new User();
    Access access = new Access();

    @BeforeEach
    void setUp() {
        card.setNumber(1L);
        card.setBalance(BigDecimal.TEN);
        card.setStatus("test");
        card.setType(Type.PERSONAL);
        card.setPaySystem(PaySystem.VISA);
        card.setCompany(new Company());

        access.setPermission("test");

        user.setId(1L);
        user.setAccessSet(Set.of(access));
        user.setStatus(Status.ACTIVE);
        user.setKeycloakUserId("test");
        user.setName("test");
        user.setPassword("test");
        user.setCompany(new Company());
        user.setRole(new Role());

        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void whenGetAllCard_thenReturnRightCard() {
        when(cardService.findCardsByCompany(any(HttpServletRequest.class))).thenReturn(List.of(CardResponseDto.fromCard(card)));

        given()
                .when().get("/api/v1/accountant/cards")
                .then().statusCode(200)
                .body("$.size()", equalTo(1))
                .body("[0].number", equalTo(1))
                .body("[0].status", equalTo("test"));
    }

    @Test
    void whenGetAllCardEmptyList_thenReturnNoContent() {
        when(cardService.findCardsByCompany(any(HttpServletRequest.class))).thenReturn(Collections.emptyList());

        given()
                .when().get("/api/v1/accountant/cards")
                .then().statusCode(204);
    }

    @Test
    void whenRegisterUserInCompany_thenReturnRegisteredUser() {
        when(userService.saveInCompany(any(AccountantRegisterUserDto.class), any(HttpServletRequest.class))).thenReturn(user);

        given()
                .contentType("application/json")
                .body(new AccountantRegisterUserDto("test", "test"))

                .when()
                .post("/api/v1/accountant/register/users")

                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    void whenChangeUserPermission_thenReturnUserWithNewPermission() {
        when(userService.changePermission(any(), any(ChangeUserPermissionDto.class))).thenReturn(user);

        given()
                .contentType("application/json")
                .body(new ChangeUserPermissionDto())

                .when()
                .post("/api/v1/accountant/users/1/permissions")

                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    void whenOrderCard_thenReturnRightCard() {
        when(cardService.orderCard(any(CardOrderDto.class), any(HttpServletRequest.class))).thenReturn(card);

        given()
                .contentType("application/json")
                .body(new CardOrderDto(Type.PERSONAL, PaySystem.VISA, 1L))

                .when()
                .post("/api/v1/accountant/cards/order")

                .then()
                .statusCode(200)
                .body("number", equalTo(1));
    }

    @Test
    void whenAddUsersInCard_thenReturnRightCard() {
        when(cardService.addUser(any(), any(ChangeUsersInCardDto.class))).thenReturn(card);

        given()
                .contentType("application/json")
                .body(new ChangeUsersInCardDto())

                .when()
                .post("/api/v1/accountant/cards/1/change")

                .then()
                .statusCode(200)
                .body("number", equalTo(1));
    }
}