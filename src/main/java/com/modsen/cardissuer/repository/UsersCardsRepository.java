package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.model.UsersCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersCardsRepository extends JpaRepository<UsersCards, Long> {

    Optional<List<UsersCards>> findByUserId(User user);

    @Transactional
    @Modifying
    @Query("update UsersCards uc set uc.card = ?1 where uc.id = ?2")
    void updateCard(Card card, Long usersCardsId);

    @Transactional
    @Modifying
    @Query("update UsersCards uc set uc.user = ?1 where uc.card = ?2")
    void addUsersInCard(User user, Card card);
}