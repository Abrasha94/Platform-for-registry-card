package com.modsen.cardissuer.rest;

import com.modsen.cardissuer.dto.response.CardResponse;
import com.modsen.cardissuer.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user/")
public class UserRestControllerV1 {

    private final CardService cardService;

    @Autowired
    public UserRestControllerV1(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("cards")
    public ResponseEntity<List<CardResponse>> getAllCard(HttpServletRequest request) {
        final List<CardResponse> cards = cardService.findCardsByUser(request);

        return new ResponseEntity<>(cards, HttpStatus.OK);
    }
}
