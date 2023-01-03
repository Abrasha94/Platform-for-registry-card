package com.modsen.cardissuer.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Balance {

    private BigDecimal balance;
    private Long cardNumber;

}
