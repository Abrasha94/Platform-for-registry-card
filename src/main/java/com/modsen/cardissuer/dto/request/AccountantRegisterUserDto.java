package com.modsen.cardissuer.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class AccountantRegisterUserDto {
    @NotEmpty
    @Size(min = 3, max = 32)
    private String name;
    @NotEmpty
    @Size(min = 6, max = 32)
    private String password;
}
