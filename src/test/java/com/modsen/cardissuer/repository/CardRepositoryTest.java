package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CardRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CompanyRepository companyRepository;

    Card card = new Card();
    Company company = new Company();

    @BeforeEach
    void setUp() {
        company.setId(1L);
        company.setName("test");
        company.setStatus(Status.ACTIVE);
        card.setNumber(123L);
        card.setStatus("test");
        card.setType(Type.PERSONAL);
        card.setPaySystem(PaySystem.MASTERCARD);
        card.setCompany(company);
        companyRepository.save(company);
        cardRepository.save(card);
    }

    @Test
    void whenSaveCard_thenReturnRightCard() {
        card.setNumber(321L);
        final Card savedCard = cardRepository.save(card);

        assertThat(savedCard.getNumber()).isEqualTo(321L);
        assertThat(savedCard.getStatus()).isEqualTo("test");
        assertThat(savedCard.getCompany().getId()).isEqualTo(1L);
    }

    @Test
    @Transactional
    void whenFindByNumber_thenReturnCard() {
        final Optional<Card> byId = cardRepository.findById(123L);

        assertThat(byId).isPresent();
        assertThat(byId.get().getStatus()).isEqualTo("test");
    }

    @Test
    void whenFindCardByCompany_thenCardFounded() {
        final List<Card> cardList = cardRepository.findByCompany(company);
        final Card foundedCard = cardList.get(0);

        assertThat(foundedCard).isEqualTo(card);
    }

    @Test
    void whenUpdateBalanceById_thenCardUpdated() {
        cardRepository.updateCardBalance(BigDecimal.TEN, 123L);
        final Card byId = cardRepository.findById(123L).get();

        assertThat(byId.getBalance()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void whenDeleteById_thenCardDeleted() {
        cardRepository.deleteById(123L);

        final Optional<Card> cardById = cardRepository.findById(123L);

        assertThat(cardById).isEmpty();
    }
}