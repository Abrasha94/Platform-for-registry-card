package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.PaySystem;
import com.modsen.cardissuer.model.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CardRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    CardRepository cardRepository;

    Card card = new Card();
    Company company = new Company();

    @BeforeEach
    void setUp() {
        company.setId(1L);
        card.setNumber(123L);
        card.setStatus("test");
        card.setType(Type.PERSONAL);
        card.setPaySystem(PaySystem.MASTERCARD);
        card.setCompany(company);
    }

    @Test
    void should_create_card() {
        final Card savedCard = cardRepository.save(card);
        assertEquals(123L, savedCard.getNumber());
        assertEquals("test", savedCard.getStatus());
        assertEquals(1L, savedCard.getCompany().getId());
    }

    @Test
    void should_find_card_by_id() {
        final Card savedCard = entityManager.persist(card);
        final Card foundedCard = cardRepository.findById(123L).get();
        assertEquals(savedCard,foundedCard);
    }

    @Test
    void should_find_card_by_company() {
        final Card savedCard = entityManager.persist(card);
        final List<Card> cardList = cardRepository.findByCompany(company);
        final Card foundedCard = cardList.get(0);
        assertEquals(savedCard, foundedCard);
    }

    @Test
    void should_update_card_by_id() {
        entityManager.persist(card);
        final Card foundedCard = cardRepository.findById(123L).get();
        foundedCard.setStatus("update status");
        cardRepository.save(foundedCard);
        final Card changedCard = cardRepository.findById(123L).get();
        assertEquals(foundedCard.getNumber(), changedCard.getNumber());
        assertEquals("update status", changedCard.getStatus());
    }

    @Test
    void should_delete_card_by_id() {
        entityManager.persist(card);
        cardRepository.deleteById(123L);
        final Optional<Card> cardById = cardRepository.findById(123L);
        assertTrue(cardById.isEmpty());
    }
}