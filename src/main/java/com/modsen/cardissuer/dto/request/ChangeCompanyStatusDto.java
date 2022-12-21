package com.modsen.cardissuer.dto.request;

import com.modsen.cardissuer.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCompanyStatusDto {

    @NotEmpty
    private Status status;
}
