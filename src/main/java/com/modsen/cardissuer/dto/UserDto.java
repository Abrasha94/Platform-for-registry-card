package com.modsen.cardissuer.dto;

import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.Role;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserDto {
    private Long Id;
    @NotBlank
    @Size(min = 3, max = 32)
    private String name;
    @NotBlank
    @Size(min = 6, max = 32)
    private String password;
    @NotBlank
    @Size(min = 6, max = 32)
    private String confirmPassword;
    private Company company;
    private Role role;
}
