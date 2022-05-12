package com.modsen.cardissuer.dto.request;


import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
public class ChangePasswordDto {

    @NotEmpty
    private String oldPass;
    @NotEmpty
    private String newPass;
}
