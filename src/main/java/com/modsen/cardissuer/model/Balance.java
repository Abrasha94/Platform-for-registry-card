package com.modsen.cardissuer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Balance {

    private BigDecimal balance;
    private Long cardNumber;

}
