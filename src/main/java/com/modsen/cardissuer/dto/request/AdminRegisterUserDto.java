package com.modsen.cardissuer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
public class AdminRegisterUserDto {
    @NotEmpty
    @Size(min = 3, max = 32)
    private String name;
    @NotEmpty
    @Size(min = 6, max = 32)
    private String password;
    @NotEmpty
    private Long companiesId;
    @NotEmpty
    private Long roleId;
    @NotEmpty
    private Set<Long> accessId;
}
