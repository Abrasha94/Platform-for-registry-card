package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByCompany(Company company);

}