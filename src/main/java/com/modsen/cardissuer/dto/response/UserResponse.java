package com.modsen.cardissuer.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    private Long id;
    private String name;
    private String company;
    private String role;
    private String[] permissions;
    private Status status;


    public static UserResponse fromUser(User user) {
        final UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setRole(user.getRole().getName());
        userResponse.setCompany(user.getCompany().getName());
        userResponse.setPermissions(user.getAccessSet().stream().map(Access::getPermission).toArray(String[]::new));
        userResponse.setStatus(user.getStatus());
        return userResponse;
    }
}
