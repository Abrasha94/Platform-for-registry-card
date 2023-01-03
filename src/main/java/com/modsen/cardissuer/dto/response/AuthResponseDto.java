package com.modsen.cardissuer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String name;
    private String token;
    private String refreshToken;
}
