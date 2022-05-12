package com.modsen.cardissuer.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.model.UsersCards;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardResponseDto {
    private Long number;
    private String status;
    private String type;
    private String paySystem;
    private List<String> users;
    private String company;

    public static CardResponseDto fromCard(Card card) {
        final CardResponseDto cardResponseDto = new CardResponseDto();
        cardResponseDto.setNumber(card.getNumber());
        cardResponseDto.setStatus(card.getStatus());
        cardResponseDto.setType(card.getType().toString());
        cardResponseDto.setPaySystem(card.getPaySystem().toString());
        final List<UsersCards> usersCards = card.getUsersCards();
        if (usersCards == null) {
            cardResponseDto.setUsers(null);
        } else {
            cardResponseDto.setUsers(usersCards.stream().map(UsersCards::getUser).map(User::getName).collect(Collectors.toList()));
        }
        cardResponseDto.setCompany(card.getCompany().getName());
        return cardResponseDto;
    }
}
