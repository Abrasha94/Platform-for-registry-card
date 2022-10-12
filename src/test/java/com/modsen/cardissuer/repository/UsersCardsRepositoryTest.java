package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsersCardsRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UsersCardsRepository usersCardsRepository;

    UsersCards usersCards = new UsersCards();
    User user = new User();
    Card card = new Card();


    @BeforeEach
    void setUp() {
        user.setId(1L);
        card.setNumber(123L);
        card.setStatus("test status");
        card.setType(Type.PERSONAL);
        card.setPaySystem(PaySystem.MASTERCARD);
        usersCards.setUser(user);
        usersCards.setCard(card);
    }

    @Test
    void should_create_users_card() {
        final UsersCards savedUsersCard = usersCardsRepository.save(usersCards);
        assertEquals(card, savedUsersCard.getCard());
        assertEquals(user, savedUsersCard.getUser());
    }

    @Test
    void should_find_users_card_by_id() {
        final UsersCards savedUsersCard = entityManager.persist(usersCards);
        final UsersCards foundedUsersCard = usersCardsRepository.findById(savedUsersCard.getId()).get();
        assertEquals(savedUsersCard, foundedUsersCard);
    }

    @Test
    void should_find_users_card_by_user_id() {
        entityManager.persist(card);
        final UsersCards savedUsersCard = entityManager.persist(usersCards);
        final List<UsersCards> cardsList = usersCardsRepository.findByUserId(user.getId()).get();
        assertEquals(savedUsersCard, cardsList.get(0));
    }

    @Test
    void should_delete_users_card_by_id() {
        final UsersCards savedUsersCard = entityManager.persist(usersCards);
        usersCardsRepository.deleteById(savedUsersCard.getId());
        final Optional<UsersCards> usersCardById = usersCardsRepository.findById(savedUsersCard.getId());
        assertTrue(usersCardById.isEmpty());
    }
}