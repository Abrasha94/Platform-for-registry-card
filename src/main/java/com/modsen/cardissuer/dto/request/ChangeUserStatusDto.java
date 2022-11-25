package com.modsen.cardissuer.dto.request;


import com.modsen.cardissuer.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
public class ChangeUserStatusDto {

    @NotEmpty
    private Status status;
}
