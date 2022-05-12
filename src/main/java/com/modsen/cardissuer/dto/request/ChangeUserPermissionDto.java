package com.modsen.cardissuer.dto.request;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
public class ChangeUserPermissionDto {

    @NotEmpty
    private Set<@Min(4L) Long> accessId;
}
