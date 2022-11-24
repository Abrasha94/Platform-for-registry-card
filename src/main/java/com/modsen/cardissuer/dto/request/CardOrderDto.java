package com.modsen.cardissuer.dto.request;

import com.modsen.cardissuer.model.PaySystem;
import com.modsen.cardissuer.model.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;


@Getter
@AllArgsConstructor
public class CardOrderDto {
    @NotEmpty
    private Type type;
    @NotEmpty
    private PaySystem paySystem;

    private Long userId;
}
