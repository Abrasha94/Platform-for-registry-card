package com.modsen.cardissuer.service;

import com.modsen.cardissuer.dto.request.CardOrderDto;
import com.modsen.cardissuer.dto.response.CardResponseDto;
import com.modsen.cardissuer.dto.request.ChangeUsersInCardDto;
import com.modsen.cardissuer.exception.BalanceNotFoundException;
import com.modsen.cardissuer.exception.CardNotFoundException;
import com.modsen.cardissuer.exception.UserNotFoundException;
import com.modsen.cardissuer.model.*;
import com.modsen.cardissuer.repository.CardRepository;
import com.modsen.cardissuer.repository.UserRepository;
import com.modsen.cardissuer.repository.UsersCardsRepository;
import com.modsen.cardissuer.util.GenerateCardNumber;
import com.modsen.cardissuer.util.MethodsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final UsersCardsRepository usersCardsRepository;
    private final UsersCardsService usersCardsService;
    private final MethodsUtil methodsUtil;
    private final GenerateCardNumber generateCardNumber;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public CardService(CardRepository cardRepository, UserRepository userRepository, MethodsUtil methodsUtil,
                       GenerateCardNumber generateCardNumber, UsersCardsRepository usersCardsRepository,
                       UsersCardsService usersCardsService, KafkaTemplate<String, String> kafkaTemplate) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.methodsUtil = methodsUtil;
        this.generateCardNumber = generateCardNumber;
        this.usersCardsRepository = usersCardsRepository;
        this.usersCardsService = usersCardsService;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<CardResponseDto> findCardsByCompany(HttpServletRequest request) {
        final User user = methodsUtil.getUserFromRequest(request);
        final List<Card> cardsByCompany = cardRepository.findByCompany(user.getCompany());
        if (cardsByCompany == null) {
            throw new CardNotFoundException("Cards not found!");
        } else {
            final List<Card> cardsWithBalance = addBalanceToCard(cardsByCompany);
            return cardsWithBalance.stream().map(CardResponseDto::fromCard).collect(Collectors.toList());
        }
    }

    public List<CardResponseDto> findCardsByUser(HttpServletRequest request) {
        final User user = methodsUtil.getUserFromRequest(request);
        final List<UsersCards> usersCards = usersCardsRepository.findByUserId(user.getId()).orElse(null);
        if (usersCards == null) {
            throw new CardNotFoundException("Cards not found!");
        } else {
            final List<Card> cards = usersCards.stream().map(UsersCards::getCard).collect(Collectors.toList());
            final List<Card> cardsWithBalance = addBalanceToCard(cards);
            return cardsWithBalance.stream().map(CardResponseDto::fromCard).collect(Collectors.toList());
        }
    }

    public Card orderCard(CardOrderDto dto, HttpServletRequest request) {
        final User user = methodsUtil.getUserFromRequest(request);
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

    public Card addUser(Long cardNumber, ChangeUsersInCardDto dto) {
        final Card card = cardRepository.findById(cardNumber).orElse(null);
        if (card == null) {
            throw new CardNotFoundException("Card not found!");
        } else if (Type.PERSONAL.equals(card.getType())) {
            throw new CardNotFoundException("This card are personal!");
        } else {
            final List<User> users = dto.getUsersId().stream()
                    .map(userRepository::getOne)
                    .collect(Collectors.toList());
            final List<UsersCards> usersCards = users.stream()
                    .map(user -> usersCardsService.save(user, card))
                    .collect(Collectors.toList());
            card.setUsersCards(usersCards);
            return card;
        }
    }

    private List<Card> addBalanceToCard(List<Card> cards) {
        final RestTemplate restTemplate = new RestTemplate();
        for (Card card : cards) {
            final Long cardNumber = card.getNumber();
            sendMsg(cardNumber.toString());
            final ResponseEntity<Balance> responseEntity = restTemplate
                    .getForEntity("http://localhost:8082/api/v1/balance/" + cardNumber, Balance.class);
            if (responseEntity.getStatusCode() != HttpStatus.OK || responseEntity.getBody() == null) {
                throw new BalanceNotFoundException("Balance do not found");
            } else {
                card.setBalance(responseEntity.getBody().getBalance());
            }
        }
        return cards;
    }

    public void sendMsg(String msg) {
        kafkaTemplate.send("balanceRequest", msg);
    }

    @KafkaListener(topics = "balanceResponse")
    public void msgListener(Balance balance) {
        final Optional<Card> optionalCard = cardRepository.findById(balance.getCardNumber());
        if (optionalCard.isPresent()) {
            cardRepository.updateCardBalance(balance.getBalance(), balance.getCardNumber());
        }
    }
}
