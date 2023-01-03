package com.modsen.cardissuer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Balance implements Serializable {

    private BigDecimal balance;
    private Long cardNumber;

}
