package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CardRepository extends JpaRepository<Card, Long>{

    public List<Card> findByUser_CompanyOrCompany(Company company, Company company1);
}