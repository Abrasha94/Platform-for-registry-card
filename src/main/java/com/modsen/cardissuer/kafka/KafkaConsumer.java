package com.modsen.cardissuer.kafka;

import com.modsen.cardissuer.model.Balance;
import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class KafkaConsumer {

    private final CardRepository cardRepository;

    @Autowired
    public KafkaConsumer(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @KafkaListener(topics = "balanceResponse")
    public void msgListener(Balance balance) {
        final Optional<Card> optionalCard = cardRepository.findById(balance.getCardNumber());
        if (optionalCard.isPresent()) {
            final Card card = optionalCard.get();
            card.setBalance(balance.getBalance());
            cardRepository.save(card);
        }
    }
}
