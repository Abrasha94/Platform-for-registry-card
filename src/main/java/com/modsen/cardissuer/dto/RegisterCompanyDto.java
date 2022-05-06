package com.modsen.cardissuer.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class RegisterCompanyDto {
    @NotEmpty
    @Size(min = 3, max = 32)
    private String name;
}
