package com.modsen.cardissuer.rest;

import com.modsen.cardissuer.dto.request.ChangeCompanyStatusDto;
import com.modsen.cardissuer.dto.request.ChangeUserStatusDto;
import com.modsen.cardissuer.dto.response.CompanyResponseDto;
import com.modsen.cardissuer.dto.request.RegisterCompanyDto;
import com.modsen.cardissuer.dto.request.AdminRegisterUserDto;
import com.modsen.cardissuer.dto.response.UserResponseDto;
import com.modsen.cardissuer.exception.CompanyNotFoundException;
import com.modsen.cardissuer.exception.UserNotFoundException;
import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.service.CardService;
import com.modsen.cardissuer.service.CompanyService;
import com.modsen.cardissuer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/admin/")
public class AdminRestControllerV1 {

    Logger logger = LoggerFactory.getLogger(AdminRestControllerV1.class);

    private final UserService userService;
    private final CompanyService companyService;
    private final CardService cardService;

    @Autowired
    public AdminRestControllerV1(UserService userService, CompanyService companyService, CardService cardService) {
        this.userService = userService;
        this.companyService = companyService;
        this.cardService = cardService;
    }

    @GetMapping("users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable(name = "id") Long id) {
        final User user = userService.findById(id);
        if (user == null) {
            logger.error("Not found user by id in admin controller");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(UserResponseDto.fromUser(user), HttpStatus.OK);
    }

    @PostMapping("register/companies")
    public ResponseEntity<CompanyResponseDto> registerCompany(@RequestBody RegisterCompanyDto registerCompanyDto) {

        final Company company = companyService.save(registerCompanyDto);
        if (company == null) {
            logger.error("Not register company in admin controller");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(CompanyResponseDto.fromCompany(company), HttpStatus.OK);
    }

    @PostMapping("register/users")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody AdminRegisterUserDto adminRegisterUserDto) {

        final User user = userService.save(adminRegisterUserDto);
        if (user == null) {
            logger.error("Not register user in admin controller");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(UserResponseDto.fromUser(user), HttpStatus.OK);
    }

    @PostMapping("users/{id}/status")
    public ResponseEntity<UserResponseDto> changeUserStatus(@PathVariable(name = "id") Long id, @RequestBody ChangeUserStatusDto dto) {
        try {
            final User user = userService.changeStatus(id, dto);
            return new ResponseEntity<>(UserResponseDto.fromUser(user), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("Trouble with changing user status in admin controller");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("companies/{id}/status")
    public ResponseEntity<CompanyResponseDto> changeCompanyStatus(@PathVariable(name = "id") Long id, @RequestBody ChangeCompanyStatusDto dto) {
        try {
            final Company company = companyService.changeStatus(id, dto);
            return new ResponseEntity<>(CompanyResponseDto.fromCompany(company), HttpStatus.OK);
        } catch (CompanyNotFoundException e) {
            logger.error("Trouble with changing company status in admin controller");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("companies")
    public ResponseEntity<List<CompanyResponseDto>> getAllCompanies() {
        final List<CompanyResponseDto> companies = companyService.findAll();
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @GetMapping("users/accountants")
    public ResponseEntity<List<UserResponseDto>> getAllAccountants() {
        try {
            final List<UserResponseDto> allAccountants = userService.findAllAccountants();
            return new ResponseEntity<>(allAccountants, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("Not found accountants in admin controller");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/test")
    public String testingELK() {
        logger.info("It is test");
        return "Hello from Test";
    }
}
