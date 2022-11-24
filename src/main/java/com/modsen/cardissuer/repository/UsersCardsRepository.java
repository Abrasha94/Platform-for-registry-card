package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.UsersCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersCardsRepository extends JpaRepository<UsersCards, Long> {

    List<UsersCards> findByUserId(Long id);

}