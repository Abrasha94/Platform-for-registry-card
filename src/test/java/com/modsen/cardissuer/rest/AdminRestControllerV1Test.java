package com.modsen.cardissuer.rest;

import com.modsen.cardissuer.dto.request.AdminRegisterUserDto;
import com.modsen.cardissuer.dto.request.ChangeCompanyStatusDto;
import com.modsen.cardissuer.dto.request.ChangeUserStatusDto;
import com.modsen.cardissuer.dto.request.RegisterCompanyDto;
import com.modsen.cardissuer.dto.response.CompanyResponse;
import com.modsen.cardissuer.dto.response.UserResponse;
import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.PaySystem;
import com.modsen.cardissuer.model.Role;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.Type;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.service.CompanyService;
import com.modsen.cardissuer.service.UserService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(AdminRestControllerV1.class)
class AdminRestControllerV1Test {

    @MockBean
    UserService userService;

    @MockBean
    CompanyService companyService;

    @Autowired
    MockMvc mockMvc;

    Card card = new Card();
    User user = new User();
    Access access = new Access();
    Company company = new Company();

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

        company.setId(1L);
        company.setStatus(Status.ACTIVE);
        company.setName("test");

        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void whenGetUserById_thenReturnFoundedUser() {
        when(userService.findById(1L)).thenReturn(user);

        given()
                .when()
                .get("/api/v1/admin/users/1")

                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    void whenRegisterCompany_thenReturnRegisteredCompany() {
        when(companyService.save(any(RegisterCompanyDto.class))).thenReturn(company);

        given()
                .contentType("application/json")
                .body(new RegisterCompanyDto())

                .when()
                .post("/api/v1/admin/register/companies")

                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    void whenRegisterUser_thenReturnRegisteredUser() {
        when(userService.save(any(AdminRegisterUserDto.class))).thenReturn(user);

        given()
                .contentType("application/json")
                .body(new AdminRegisterUserDto())

                .when()
                .post("/api/v1/admin/register/users")

                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    void whenChangeUserStatus_theReturnChangedUser() {
        when(userService.changeStatus(any(), any(ChangeUserStatusDto.class))).thenReturn(user);

        given()
                .contentType("application/json")
                .body(new ChangeUserStatusDto())

                .when()
                .post("/api/v1/admin/users/1/status")

                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    void whenChangeCompanyStatus_thenReturnChangedCompany() {
        when(companyService.changeStatus(any(), any(ChangeCompanyStatusDto.class))).thenReturn(company);

        given()
                .contentType("application/json")
                .body(new ChangeCompanyStatusDto())

                .when()
                .post("/api/v1/admin/companies/1/status")

                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    void whenGetAllCompanies_thenReturnFoundedCompany() {
        when(companyService.findAll()).thenReturn(List.of(CompanyResponse.fromCompany(company)));

        given()
                .when()
                .get("/api/v1/admin/companies")

                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1))
                .body("[0].id", equalTo(1));
    }

    @Test
    void whenGetAllAccountants_thenReturnFoundedUser() {
        when(userService.findAllAccountants()).thenReturn(List.of(UserResponse.fromUser(user)));

        given()
                .when()
                .get("/api/v1/admin/users/accountants")

                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1))
                .body("[0].id", equalTo(1));
    }
}