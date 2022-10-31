package com.modsen.cardissuer.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class MethodsUtil {

    public String getKeycloakUserId(HttpServletRequest request) {

        return request.getHeader("keycloakUserID");
    }
}
