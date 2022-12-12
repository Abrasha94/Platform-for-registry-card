package com.modsen.cardissuer.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponseDto {
    private Long id;
    private String name;
    private String company;
    private Status status;


    public static UserResponseDto fromUser(User user) {
        final UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setName(user.getName());
        userResponseDto.setCompany(user.getCompany().getName());
        userResponseDto.setStatus(user.getStatus());
        return userResponseDto;
    }
}
