package com.modsen.cardissuer.dto.request;

import com.modsen.cardissuer.model.PaySystem;
import com.modsen.cardissuer.model.Type;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;


@Getter
public class CardOrderDto {
    @NotEmpty
    private Type type;
    @NotEmpty
    private PaySystem paySystem;

    private Long userId;
}
