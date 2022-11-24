package com.modsen.cardissuer.service;

import com.modsen.cardissuer.client.BalanceClient;
import com.modsen.cardissuer.dto.request.CardOrderDto;
import com.modsen.cardissuer.dto.request.ChangeUsersInCardDto;
import com.modsen.cardissuer.dto.response.CardResponseDto;
import com.modsen.cardissuer.model.*;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void whenOrderCard_thenReturnRightCard() {
        final CardOrderDto cardOrderDto = new CardOrderDto(Type.PERSONAL, PaySystem.VISA, 1L);
        final User user = new User();

        when(userRepository.findByKeycloakUserId(any())).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.save(any())).thenReturn(card1);
        when(generateCardNumber.generateVisa()).thenReturn(666L);
        when(usersCardsRepository.save(any())).thenReturn(new UsersCards());

        final Card card = cardService.orderCard(cardOrderDto, request);

        Mockito.verify(userRepository, times(1)).findByKeycloakUserId(any());
        Mockito.verify(cardRepository, times(1)).save(any());
        assertThat(card).isEqualTo(card1);
    }

    @Test
    void whenAddUserToCard_thenReturnRightCard() {
        final ChangeUsersInCardDto cardDto = new ChangeUsersInCardDto(List.of(1L, 2L));
        final User user = new User();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card2));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(usersCardsService.save(any(), any())).thenReturn(new UsersCards());

        final Card card = cardService.addUser(1L, cardDto);

        Mockito.verify(userRepository, times(2)).findById(any());
        Mockito.verify(cardRepository, times(1)).findById(1L);
        assertThat(card.getNumber()).isEqualTo(2L);
    }
}