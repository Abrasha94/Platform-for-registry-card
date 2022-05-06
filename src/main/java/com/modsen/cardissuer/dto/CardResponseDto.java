package com.modsen.cardissuer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.User;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardResponseDto {
    private Long number;
    private String status;
    private String type;
    private Timestamp expirationDate;
    private List<String> users;
    private String company;

    public static CardResponseDto fromCard(Card card) {
        final CardResponseDto cardResponseDto = new CardResponseDto();
        cardResponseDto.setNumber(card.getNumber());
        cardResponseDto.setStatus(card.getStatus());
        cardResponseDto.setType(card.getType());
        if (card.getUsers() == null) {
            cardResponseDto.setUsers(null);
        } else {
            cardResponseDto.setUsers(card.getUsers().stream().map(User::getName).collect(Collectors.toList()));
        }
        if (card.getCompany() == null) {
            cardResponseDto.setCompany(null);
        } else {
            cardResponseDto.setCompany(card.getCompany().getName());
        }
        return cardResponseDto;
    }
}
