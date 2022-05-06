package com.modsen.cardissuer.dto;

import com.modsen.cardissuer.model.Status;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
public class ChangeCompanyStatusDto {

    @NotEmpty
    private Status status;
}
