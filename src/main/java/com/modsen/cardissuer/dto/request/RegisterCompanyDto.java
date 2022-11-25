package com.modsen.cardissuer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class RegisterCompanyDto {
    @NotEmpty
    @Size(min = 3, max = 32)
    private String name;
}
