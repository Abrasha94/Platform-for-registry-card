package com.modsen.cardissuer.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;
}
