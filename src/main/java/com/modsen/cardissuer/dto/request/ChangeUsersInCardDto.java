package com.modsen.cardissuer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeUsersInCardDto {

    @NotEmpty
    private List<Long> usersId;
}
