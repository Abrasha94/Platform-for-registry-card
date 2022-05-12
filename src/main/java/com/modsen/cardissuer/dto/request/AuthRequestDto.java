package com.modsen.cardissuer.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String password;
}
