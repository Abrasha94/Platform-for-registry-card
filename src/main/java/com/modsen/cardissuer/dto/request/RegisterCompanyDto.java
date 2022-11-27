package com.modsen.cardissuer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterCompanyDto {
    @NotEmpty
    @Size(min = 3, max = 32)
    private String name;
}
