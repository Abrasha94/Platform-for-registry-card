package com.modsen.cardissuer.service;

import com.modsen.cardissuer.client.BalanceClient;
import com.modsen.cardissuer.kafka.KafkaProducer;
import com.modsen.cardissuer.dto.request.CardOrderDto;
import com.modsen.cardissuer.dto.response.CardResponse;
import com.modsen.cardissuer.dto.request.ChangeUsersInCardDto;
import com.modsen.cardissuer.exception.CardNotFoundException;
import com.modsen.cardissuer.exception.UserNotFoundException;
import com.modsen.cardissuer.model.Balance;
import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.PaySystem;
import com.modsen.cardissuer.model.Type;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.model.UsersCards;
import com.modsen.cardissuer.repository.CardRepository;
import com.modsen.cardissuer.repository.UserRepository;
import com.modsen.cardissuer.repository.UsersCardsRepository;
import com.modsen.cardissuer.rest.AccountantRestControllerV1;
import com.modsen.cardissuer.util.GenerateCardNumber;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    private Logger logger = LoggerFactory.getLogger(AccountantRestControllerV1.class);

    @Value("${exception.user.not.found}")
    private String userNotFound;
    public static final String HEADER_KEYCLOAKUSERID = "keycloakUserID";
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final UsersCardsRepository usersCardsRepository;
    private final UsersCardsService usersCardsService;
    private final GenerateCardNumber generateCardNumber;
    private final KafkaProducer kafkaProducer;
    private final BalanceClient balanceClient;

    @Autowired
    public CardService(CardRepository cardRepository, UserRepository userRepository,
                       GenerateCardNumber generateCardNumber, UsersCardsRepository usersCardsRepository,
                       UsersCardsService usersCardsService, KafkaProducer kafkaProducer, BalanceClient balanceClient) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.generateCardNumber = generateCardNumber;
        this.usersCardsRepository = usersCardsRepository;
        this.usersCardsService = usersCardsService;
        this.kafkaProducer = kafkaProducer;
        this.balanceClient = balanceClient;
    }

    public List<CardResponse> findCardsByCompany(HttpServletRequest request) {

        final User user = userRepository.findByKeycloakUserId(request.getHeader(HEADER_KEYCLOAKUSERID))
                .orElseThrow(() -> new UserNotFoundException(userNotFound));

        final List<Card> cardsByCompany = cardRepository.findByCompany(user.getCompany());
        if (cardsByCompany.isEmpty()) {
            throw new CardNotFoundException("Cards not found!");
        } else {
            final List<Card> cardsWithBalance = addBalanceToListOfCards(cardsByCompany);
            return cardsWithBalance.stream().map(CardResponse::fromCard).collect(Collectors.toList());
        }
    }

    public List<CardResponse> findCardsByUser(HttpServletRequest request) {

        final User user = userRepository.findByKeycloakUserId(request.getHeader(HEADER_KEYCLOAKUSERID))
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        final List<UsersCards> usersCardsList = usersCardsRepository.findByUserId(user.getId());

        if (usersCardsList.isEmpty()) {
            throw new CardNotFoundException("Cards not found!");
        } else {
            final List<Card> cards = usersCardsList.stream().map(UsersCards::getCard).collect(Collectors.toList());
            final List<Card> cardsWithBalance = addBalanceToListOfCards(cards);
            return cardsWithBalance.stream().map(CardResponse::fromCard).collect(Collectors.toList());
        }
    }

    public Card orderCard(CardOrderDto dto, HttpServletRequest request) {

        final User requestUser = userRepository.findByKeycloakUserId(request.getHeader(HEADER_KEYCLOAKUSERID))
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

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
        card.setCompany(requestUser.getCompany());

        if (dto.getUserId() == null) {
            card.setUsersCards(null);
            return cardRepository.save(card);
        } else {
            final User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found!"));

            final UsersCards usersCards = new UsersCards();
            usersCards.setUser(user);

            card.setUsersCards(Collections.singletonList(usersCards));

            final Card savedCard = cardRepository.save(card);
            usersCardsRepository.save(usersCards);
            return savedCard;
        }
    }

    public Card addUser(Long cardNumber, ChangeUsersInCardDto dto) {

        final Card card = cardRepository.findById(cardNumber).orElseThrow(() -> new CardNotFoundException("Card not found!"));

        if (Type.PERSONAL.equals(card.getType())) {
            throw new CardNotFoundException("This card are personal!");
        } else {
            final List<User> users = dto.getUsersId().stream()
                    .map(id -> userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found!")))
                    .collect(Collectors.toList());
            final List<UsersCards> usersCards = users.stream()
                    .map(user -> usersCardsService.save(user, card))
                    .collect(Collectors.toList());
            card.setUsersCards(usersCards);
            return card;
        }
    }

    private List<Card> addBalanceToListOfCards(List<Card> cards) {

        for (Card card : cards) {
            final Long cardNumber = card.getNumber();
            sendMsg("balanceRequest", cardNumber.toString());
            card.setBalance(getBalanceFromCard(cardNumber).getBalance());
        }

        return cards;
    }

    @HystrixCommand(fallbackMethod = "fallbackBalance", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
    })
    @Cacheable("balanceCache")
    public Balance getBalanceFromCard(Long cardNumber) {

        final ResponseEntity<Balance> response = balanceClient.getBalance(cardNumber);
        if (response.getBody() == null || response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new CardNotFoundException("Card not found!");
        }
        return response.getBody();
    }

    private Balance fallbackBalance(Long cardNumber) {
        final Card card = cardRepository.findById(cardNumber).orElseThrow(() -> new CardNotFoundException("Card not found!"));
        return new Balance(card.getBalance(), cardNumber);
    }

    public void sendMsg(String topic, String msg) {
        kafkaProducer.send(topic, msg);
    }
}
