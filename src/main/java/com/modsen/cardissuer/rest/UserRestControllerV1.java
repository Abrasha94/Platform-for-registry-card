package com.modsen.cardissuer.rest;

import com.modsen.cardissuer.dto.response.CardResponseDto;
import com.modsen.cardissuer.exception.CardNotFoundException;
import com.modsen.cardissuer.service.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(UserRestControllerV1.class);

    private final CardService cardService;

    @Autowired
    public UserRestControllerV1(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("cards")
    public ResponseEntity<List<CardResponseDto>> getAllCard(HttpServletRequest request) {
        try {
            final List<CardResponseDto> cards = cardService.findCardsByUser(request);
            return new ResponseEntity<>(cards, HttpStatus.OK);
        } catch (CardNotFoundException e) {
            logger.error("Not found cards in user controller for user!");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
