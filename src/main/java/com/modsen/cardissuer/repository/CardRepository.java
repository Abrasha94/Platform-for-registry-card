package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByCompany(Company company);

    @Transactional
    @Modifying
    @Query(value = "update Card c set c.balance = ?1 where c.number = ?2")
    void updateCardBalance(BigDecimal balance, Long cardNumber);

}