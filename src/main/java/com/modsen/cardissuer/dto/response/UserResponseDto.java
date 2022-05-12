package com.modsen.cardissuer.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponseDto {
    private Long id;
    private String name;
    private String company;
    private String role;
    private String[] permissions;
    private Status status;

//    public User toUser() {
//        final User user = new User();
//        user.setId(id);
//        user.setName(name);
//        user.setCompany(company);
//        user.setRole(role);
//        user.setAccessSet(accessSet);
//        return user;
//    }

    public static UserResponseDto fromUser(User user) {
        final UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setName(user.getName());
        userResponseDto.setRole(user.getRole().getName());
        userResponseDto.setCompany(user.getCompany().getName());
        userResponseDto.setPermissions(user.getAccessSet().stream().map(Access::getPermission).toArray(String[]::new));
        userResponseDto.setStatus(user.getStatus());
        return userResponseDto;
    }
}
