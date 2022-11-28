package com.modsen.cardissuer.service;

import com.modsen.cardissuer.client.BalanceClient;
import com.modsen.cardissuer.dto.request.CardOrderDto;
import com.modsen.cardissuer.dto.request.ChangeUsersInCardDto;
import com.modsen.cardissuer.dto.response.CardResponseDto;
import com.modsen.cardissuer.exception.CardNotFoundException;
import com.modsen.cardissuer.exception.UserNotFoundException;
import com.modsen.cardissuer.model.Balance;
import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.PaySystem;
import com.modsen.cardissuer.model.Type;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.model.UsersCards;
import com.modsen.cardissuer.repository.CardRepository;
import com.modsen.cardissuer.repository.UserRepository;
import com.modsen.cardissuer.repository.UsersCardsRepository;
import com.modsen.cardissuer.util.GenerateCardNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.concurrent.ListenableFuture;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @InjectMocks
    CardService cardService;

    @Mock
    CardRepository cardRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UsersCardsRepository usersCardsRepository;

    @Mock
    UsersCardsService usersCardsService;

    @Mock
    GenerateCardNumber generateCardNumber;

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    BalanceClient balanceClient;

    Card card1 = new Card();
    Card card2 = new Card();
    UsersCards usersCards = new UsersCards();
    HttpServletRequest request = new MockHttpServletRequest();

    @BeforeEach
    void setUp() {
        card1.setNumber(1L);
        card1.setBalance(BigDecimal.TEN);
        card1.setStatus("test");
        card1.setType(Type.PERSONAL);
        card1.setPaySystem(PaySystem.VISA);
        card1.setCompany(new Company());

        card2.setNumber(2L);
        card2.setStatus("test");
        card2.setType(Type.CORPORATE);
        card2.setPaySystem(PaySystem.VISA);
        card2.setCompany(new Company());

        usersCards.setCard(card1);
    }

    @Test
    void whenFindCardsByCompany_thenReturnRightDto() {
        when(userRepository.findByKeycloakUserId(any())).thenReturn(Optional.of(new User()));
        when(cardRepository.findByCompany(any())).thenReturn(List.of(card1, card2));
        when(kafkaTemplate.send(any(), any())).thenReturn(mock(ListenableFuture.class));
        when(balanceClient.getBalance(any())).thenReturn(new ResponseEntity<>(new Balance(BigDecimal.TEN, 1L), HttpStatus.OK));

        final List<CardResponseDto> cardsByCompany = cardService.findCardsByCompany(request);

        Mockito.verify(userRepository, times(1)).findByKeycloakUserId(any());
        Mockito.verify(cardRepository, times(1)).findByCompany(any());
        assertThat(cardsByCompany.size()).isEqualTo(2);
    }

    @Test
    void whenFindCardsByCompanyIsBad_thenThrowExceptions() {
        when(userRepository.findByKeycloakUserId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.findCardsByCompany(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");

        when(userRepository.findByKeycloakUserId(any())).thenReturn(Optional.of(new User()));
        when(cardRepository.findByCompany(any())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> cardService.findCardsByCompany(request))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessage("Cards not found!");

        when(userRepository.findByKeycloakUserId(any())).thenReturn(Optional.of(new User()));
        when(cardRepository.findByCompany(any())).thenReturn(List.of(card1, card2));
        when(kafkaTemplate.send(any(), any())).thenReturn(mock(ListenableFuture.class));
        when(balanceClient.getBalance(any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        assertThatThrownBy(() -> cardService.findCardsByCompany(request))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessage("Card not found!");
    }

    @Test
    void whenFindCardsByUser_thenReturnRightDto() {
        when(userRepository.findByKeycloakUserId(any())).thenReturn(Optional.of(new User()));
        when(usersCardsRepository.findByUserId(any())).thenReturn(List.of(usersCards));
        when(kafkaTemplate.send(any(), any())).thenReturn(mock(ListenableFuture.class));
        when(balanceClient.getBalance(any())).thenReturn(new ResponseEntity<>(new Balance(BigDecimal.TEN, 1L), HttpStatus.OK));

        final List<CardResponseDto> cardsByCompany = cardService.findCardsByUser(request);

        Mockito.verify(userRepository, times(1)).findByKeycloakUserId(any());
        Mockito.verify(usersCardsRepository, times(1)).findByUserId(any());
        assertThat(cardsByCompany.size()).isEqualTo(1);
    }

    @Test
    void whenFindCardsByUserIsBad_thenThrowExceptions() {
        when(userRepository.findByKeycloakUserId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.findCardsByUser(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");

        when(userRepository.findByKeycloakUserId(any())).thenReturn(Optional.of(new User()));
        when(usersCardsRepository.findByUserId(any())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> cardService.findCardsByUser(request))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessage("Cards not found!");
    }

    @Test
    void whenOrderCard_thenReturnRightCard() {
        when(userRepository.findByKeycloakUserId(any())).thenReturn(Optional.of(new User()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(cardRepository.save(any())).thenReturn(card1);
        when(generateCardNumber.generateVisa()).thenReturn(666L);
        when(usersCardsRepository.save(any())).thenReturn(new UsersCards());

        final Card card = cardService.orderCard(new CardOrderDto(Type.PERSONAL, PaySystem.VISA, 1L), request);

        Mockito.verify(userRepository, times(1)).findByKeycloakUserId(any());
        Mockito.verify(cardRepository, times(1)).save(any());
        assertThat(card).isEqualTo(card1);


        when(userRepository.findByKeycloakUserId(any())).thenReturn(Optional.of(new User()));
        when(cardRepository.save(any(Card.class))).thenReturn(card1);
        when(generateCardNumber.generateVisa()).thenReturn(666L);

        final Card cardWithNullUsersCards = cardService.orderCard(new CardOrderDto(Type.PERSONAL, PaySystem.VISA, null), request);

        Mockito.verify(userRepository, times(2)).findByKeycloakUserId(any());
        Mockito.verify(cardRepository, times(2)).save(any());
        assertThat(cardWithNullUsersCards).isEqualTo(card1);
    }

    @Test
    void whenOrderCardIsBad_thenThrowExceptions() {
        when(userRepository.findByKeycloakUserId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.orderCard(new CardOrderDto(Type.PERSONAL, PaySystem.VISA, 1L), request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");

        when(userRepository.findByKeycloakUserId(any())).thenReturn(Optional.of(new User()));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.orderCard(new CardOrderDto(Type.PERSONAL, PaySystem.VISA, 1L), request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");
    }

    @Test
    void whenAddUserToCard_thenReturnRightCard() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card2));
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(usersCardsService.save(any(), any())).thenReturn(new UsersCards());

        final Card card = cardService.addUser(1L, new ChangeUsersInCardDto(List.of(1L, 2L)));

        Mockito.verify(userRepository, times(2)).findById(any());
        Mockito.verify(cardRepository, times(1)).findById(1L);
        assertThat(card.getNumber()).isEqualTo(2L);
    }

    @Test
    void whenAddUserToCardIsBad_thenThrowExceptions() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.addUser(1L, new ChangeUsersInCardDto(List.of(1L, 2L))))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessage("Card not found!");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));

        assertThatThrownBy(() -> cardService.addUser(1L, new ChangeUsersInCardDto(List.of(1L, 2L))))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessage("This card are personal!");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card2));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.addUser(1L, new ChangeUsersInCardDto(List.of(1L, 2L))))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");
    }
}