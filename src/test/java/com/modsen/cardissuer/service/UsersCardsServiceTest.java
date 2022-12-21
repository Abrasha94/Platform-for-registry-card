package com.modsen.cardissuer.service;

import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.model.UsersCards;
import com.modsen.cardissuer.repository.UsersCardsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersCardsServiceTest {

    @InjectMocks
    UsersCardsService usersCardsService;

    @Mock
    UsersCardsRepository usersCardsRepository;

    UsersCards usersCards = new UsersCards();

    @Test
    void whenSaveUsersCards_thenReturnRightUsersCards() {
        when(usersCardsRepository.save(any(UsersCards.class))).thenReturn(usersCards);

        final UsersCards savedUsersCards = usersCardsService.save(new User(), new Card());

        verify(usersCardsRepository, times(1)).save(any(UsersCards.class));
        assertThat(savedUsersCards).isEqualTo(usersCards);
    }
}