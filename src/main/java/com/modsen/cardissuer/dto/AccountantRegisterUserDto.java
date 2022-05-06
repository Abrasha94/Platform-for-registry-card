package com.modsen.cardissuer.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class AccountantRegisterUserDto {
    @NotEmpty
    @Size(min = 3, max = 32)
    private String name;
    @NotEmpty
    @Size(min = 6, max = 32)
    private String password;
}
