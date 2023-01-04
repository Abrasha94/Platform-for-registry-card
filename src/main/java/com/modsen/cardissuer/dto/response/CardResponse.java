package com.modsen.cardissuer.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.model.UsersCards;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardResponse {
    private Long number;
    private String status;
    private String type;
    private String paySystem;
    private List<String> users;
    private String company;
    private BigDecimal balance;

    public static CardResponse fromCard(Card card) {
        final CardResponse cardResponse = new CardResponse();
        cardResponse.setNumber(card.getNumber());
        cardResponse.setStatus(card.getStatus());
        cardResponse.setType(String.valueOf(card.getType()));
        cardResponse.setPaySystem(card.getPaySystem().toString());
        final List<UsersCards> usersCards = card.getUsersCards();
        if (usersCards == null) {
            cardResponse.setUsers(null);
        } else {
            cardResponse.setUsers(usersCards.stream().map(UsersCards::getUser).map(User::getName).collect(Collectors.toList()));
        }
        cardResponse.setCompany(card.getCompany().getName());
        cardResponse.setBalance(card.getBalance());
        return cardResponse;
    }
}
