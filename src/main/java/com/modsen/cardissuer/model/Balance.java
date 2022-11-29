package com.modsen.cardissuer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class Balance {

    private BigDecimal balance;
    private Long cardNumber;

}
