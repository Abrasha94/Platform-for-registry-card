package com.modsen.cardissuer.rest;

import com.modsen.cardissuer.dto.request.AccountantRegisterUserDto;
import com.modsen.cardissuer.dto.request.CardOrderDto;
import com.modsen.cardissuer.dto.response.CardResponseDto;
import com.modsen.cardissuer.dto.request.ChangeUserPermissionDto;
import com.modsen.cardissuer.dto.request.ChangeUsersInCardDto;
import com.modsen.cardissuer.dto.response.UserResponseDto;
import com.modsen.cardissuer.exception.CardNotFoundException;
import com.modsen.cardissuer.exception.UserNotFoundException;
import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.service.CardService;
import com.modsen.cardissuer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/accountant/")
public class AccountantRestControllerV1 {

    private final CardService cardService;
    private final UserService userService;

    @Autowired
    public AccountantRestControllerV1(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }

    @GetMapping("cards")
    public ResponseEntity<List<CardResponseDto>> getAllCard(HttpServletRequest request) {
        try {
            final List<CardResponseDto> cards = cardService.findCardsByCompany(request);
            return new ResponseEntity<>(cards, HttpStatus.OK);
        } catch (NoSuchElementException | CardNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("register/users")
    public ResponseEntity registerUserInCompany(@RequestBody AccountantRegisterUserDto dto, HttpServletRequest request) {
        final User user = userService.saveInCompany(dto, request);
        if (user == null) {
            return ResponseEntity.badRequest().body(dto.getName() + " user do not created!");
        }
        return ResponseEntity.ok(user.getName() + " user successfully created!"
                + " Work in " + user.getCompany().getName()
                + " company, and have " + user.getRole().getName() + " role. With "
                + user.getAccessSet().stream().map(Access::getPermission).collect(Collectors.toList()));
    }

    @PostMapping("users/{id}/permissions")
    public ResponseEntity<UserResponseDto> changeUserPermission(@PathVariable(name = "id") Long id,
                                                                @RequestBody ChangeUserPermissionDto dto) {
        try {
            final User user = userService.changePermission(id, dto);
            return new ResponseEntity<>(UserResponseDto.fromUser(user), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("cards/order")
    public ResponseEntity<CardResponseDto> orderCard(@RequestBody CardOrderDto dto, HttpServletRequest request) {
        try {
            final Card card = cardService.orderCard(dto, request);
            return new ResponseEntity<>(CardResponseDto.fromCard(card), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("cards/{number}/change")
    public ResponseEntity<CardResponseDto> addUsersInCard(@PathVariable(name = "number") Long number,
                                                          @RequestBody ChangeUsersInCardDto dto,
                                                          HttpServletRequest request) {
        try {
            final Card card = cardService.addUser(number, dto, request);
            return new ResponseEntity<>(CardResponseDto.fromCard(card), HttpStatus.OK);
        } catch (CardNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
