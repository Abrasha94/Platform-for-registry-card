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
        final Company savedCompany = companyRepository.save(company);
        card.setCompany(savedCompany);
        cardRepository.save(card);
    }

    @Test
    void whenSaveCard_thenReturnRightCard() {
        card.setNumber(321L);
        final Card savedCard = cardRepository.save(card);

        assertThat(savedCard.getNumber()).isEqualTo(321L);
        assertThat(savedCard.getStatus()).isEqualTo("test");
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
        final List<Company> companyList = companyRepository.findAll();

        assertThat(companyList).isNotEmpty();

        final List<Card> cardList = cardRepository.findByCompany(companyList.get(0));

        assertThat(cardList).isNotEmpty();

        final Card foundedCard = cardList.get(0);

        assertThat(foundedCard).isEqualTo(card);
    }

    @Test
    void whenUpdateCardById_thenCardUpdated() {
        final List<Card> all = cardRepository.findAll();

        assertThat(all).isNotEmpty();

        final Card changedCard = cardRepository.findById(all.get(0).getNumber()).get();
        changedCard.setBalance(BigDecimal.TEN);
        final Card saveCard = cardRepository.save(changedCard);

        assertThat(saveCard.getBalance()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void whenDeleteById_thenCardDeleted() {
        cardRepository.deleteById(123L);

        final Optional<Card> cardById = cardRepository.findById(123L);

        assertThat(cardById).isEmpty();
    }
}