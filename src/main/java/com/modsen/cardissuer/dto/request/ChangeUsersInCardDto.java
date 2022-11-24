package com.modsen.cardissuer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@AllArgsConstructor
public class ChangeUsersInCardDto {

    @NotEmpty
    private List<Long> usersId;
}
