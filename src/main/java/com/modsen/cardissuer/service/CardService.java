package com.modsen.cardissuer.service;

import com.modsen.cardissuer.dto.request.CardOrderDto;
import com.modsen.cardissuer.dto.response.CardResponseDto;
import com.modsen.cardissuer.dto.request.ChangeUsersInCardDto;
import com.modsen.cardissuer.exception.CardNotFoundException;
import com.modsen.cardissuer.exception.UserNotFoundException;
import com.modsen.cardissuer.model.*;
import com.modsen.cardissuer.repository.CardRepository;
import com.modsen.cardissuer.repository.UserRepository;
import com.modsen.cardissuer.repository.UsersCardsRepository;
import com.modsen.cardissuer.security.jwt.JwtTokenProvider;
import com.modsen.cardissuer.util.GenerateCardNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    public final CardRepository cardRepository;
    public final UserRepository userRepository;
    public final UsersCardsRepository usersCardsRepository;
    public final UsersCardsService usersCardsService;
    public final JwtTokenProvider jwtTokenProvider;
    public final GenerateCardNumber generateCardNumber;

    @Autowired
    public CardService(CardRepository cardRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider, GenerateCardNumber generateCardNumber, UsersCardsRepository usersCardsRepository, UsersCardsService usersCardsService) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.generateCardNumber = generateCardNumber;
        this.usersCardsRepository = usersCardsRepository;
        this.usersCardsService = usersCardsService;
    }

    public List<CardResponseDto> findCardsByCompany(HttpServletRequest request) {
        final User user = getUserFromRequest(request);
        final List<Card> cardsByCompany = cardRepository.findByCompany(user.getCompany());
        if (cardsByCompany == null) {
            throw new CardNotFoundException("Cards not found!");
        }
        return cardsByCompany.stream().map(CardResponseDto::fromCard).collect(Collectors.toList());
    }

    public List<CardResponseDto> findCardsByUser(HttpServletRequest request) {
        final User user = getUserFromRequest(request);
        final List<UsersCards> usersCards = usersCardsRepository.findByUserId(user).orElse(null);
        if (usersCards == null) {
            throw new CardNotFoundException("Cards not found!");
        } else {
            final List<Card> cards = usersCards.stream().map(UsersCards::getCard).collect(Collectors.toList());
            return cards.stream().map(CardResponseDto::fromCard).collect(Collectors.toList());
        }
    }

    public Card orderCard(CardOrderDto dto, HttpServletRequest request) {
        final User user = getUserFromRequest(request);
        final Card card = new Card();
        if (PaySystem.VISA.equals(dto.getPaySystem())) {
            card.setNumber(generateCardNumber.generateVisa());
        }
        if (PaySystem.MASTERCARD.equals(dto.getPaySystem())) {
            card.setNumber(generateCardNumber.generateMasterCard());
        }
        card.setStatus("In order");
        card.setType(dto.getType());
        card.setPaySystem(dto.getPaySystem());
        card.setCompany(user.getCompany());
        if (dto.getUserId() == null) {
            card.setUsersCards(null);
            return cardRepository.save(card);
        } else if (userRepository.findById(dto.getUserId()).isPresent()) {
            final UsersCards usersCards = new UsersCards();
            usersCards.setUser(userRepository.getOne(dto.getUserId()));
            final UsersCards saveUsersCards = usersCardsRepository.save(usersCards);
            card.setUsersCards(Collections.singletonList(saveUsersCards));
            final Card saveCard = cardRepository.save(card);
            usersCardsRepository.updateCard(saveCard, saveUsersCards.getId());
            return saveCard;
        } else {
            throw new UserNotFoundException("User with id: " + dto.getUserId() + " not found!");
        }
    }

    public Card addUser(Long cardNumber, ChangeUsersInCardDto dto, HttpServletRequest request) {
        final Card card = cardRepository.findById(cardNumber).orElse(null);
        if (card == null) {
            throw new CardNotFoundException("Card not found!");
        } else if (Type.PERSONAL.equals(card.getType())) {
            throw new CardNotFoundException("This card are personal!");
        } else {
            final List<User> users = dto.getUsersId().stream().map(userRepository::getOne).collect(Collectors.toList());
            final List<UsersCards> usersCards = users.stream()
                    .map(user -> usersCardsService.save(user, card))
                    .collect(Collectors.toList());
            card.setUsersCards(usersCards);
            return card;
        }
    }

    private User getUserFromRequest(HttpServletRequest request) {
        final String token = jwtTokenProvider.resolveToken(request);
        final String userName = jwtTokenProvider.getName(token);
        return userRepository.findByName(userName).orElseThrow();
    }
}
