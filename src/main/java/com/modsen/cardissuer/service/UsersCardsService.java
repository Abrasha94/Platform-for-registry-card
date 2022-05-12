package com.modsen.cardissuer.service;

import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.model.UsersCards;
import com.modsen.cardissuer.repository.UsersCardsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersCardsService {
    private final UsersCardsRepository usersCardsRepository;

    @Autowired
    public UsersCardsService(UsersCardsRepository usersCardsRepository) {
        this.usersCardsRepository = usersCardsRepository;
    }

    public UsersCards save(User user, Card card) {
        final UsersCards usersCards = new UsersCards();
        usersCards.setCard(card);
        usersCards.setUser(user);
        return usersCardsRepository.save(usersCards);
    }
}
