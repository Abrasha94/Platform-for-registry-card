package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.UsersCards;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class UsersCardsRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    UsersCardsRepository usersCardsRepository;

    @Autowired
    UserRepository userRepository;

    UsersCards usersCards = new UsersCards();

    @BeforeEach
    void setUp() {
        usersCardsRepository.save(usersCards);
    }

    @Test
    void whenSaveUsersCards_thenReturnRightUsersCards() {
        UsersCards usersCardsNew = new UsersCards();

        final UsersCards saveUsersCards = usersCardsRepository.save(usersCardsNew);

        assertThat(saveUsersCards).isEqualTo(usersCardsNew);
    }

    @Test
    void whenFindUsersCardsById_thenReturnUsersCards() {
        final List<UsersCards> all = usersCardsRepository.findAll();

        assertThat(all).isNotEmpty();

        final Optional<UsersCards> byId = usersCardsRepository.findById(all.get(0).getId());

        assertThat(byId).isPresent();
        assertThat(byId.get()).isEqualTo(usersCards);
    }

    @Test
    void whenUpdateUsersCardsById_thenUsersCardsUpdated() {
        final List<UsersCards> all = usersCardsRepository.findAll();

        assertThat(all).isNotEmpty();

        final UsersCards changedUsersCards = usersCardsRepository.findById(all.get(0).getId()).get();
        changedUsersCards.setId(Long.MAX_VALUE);
        final UsersCards saveUsersCards = usersCardsRepository.save(changedUsersCards);

        assertThat(saveUsersCards.getId()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void whenDeleteById_thenUsersCardsDeleted() {
        final List<UsersCards> all = usersCardsRepository.findAll();

        assertThat(all).isNotEmpty();

        usersCardsRepository.deleteById(all.get(0).getId());

        final Optional<UsersCards> byId = usersCardsRepository.findById(all.get(0).getId());

        assertThat(byId).isEmpty();
    }
}