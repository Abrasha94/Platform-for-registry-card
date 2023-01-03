package com.modsen.cardissuer.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
public class ChangeUsersInCardDto {

    @NotEmpty
    private List<Long> usersId;
}
