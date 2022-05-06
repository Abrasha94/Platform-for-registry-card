package com.modsen.cardissuer.service;

import com.modsen.cardissuer.dto.CardResponseDto;
import com.modsen.cardissuer.exception.CardNotFoundException;
import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.repository.CardRepository;
import com.modsen.cardissuer.repository.UserRepository;
import com.modsen.cardissuer.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    public final CardRepository cardRepository;
    public final UserRepository userRepository;
    public final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public CardService(CardRepository cardRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public List<CardResponseDto> findCardsByUserCompany(HttpServletRequest request) {
        final String token = jwtTokenProvider.resolveToken(request);
        final String userName = jwtTokenProvider.getName(token);
        final User user = userRepository.findByName(userName).orElseThrow();
        final List<Card> cards = cardRepository.findByUser_CompanyOrCompany(user.getCompany(), user.getCompany());
        if (cards == null) {
            throw new CardNotFoundException("Cards not found!");
        }
        return cards.stream().map(CardResponseDto::fromCard).collect(Collectors.toList());
    }
}
