package com.modsen.cardissuer.util;

import com.modsen.cardissuer.exception.UserNotFoundException;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class MethodsUtil {

//    public final UserRepository userRepository;
//    public final JwtTokenProvider jwtTokenProvider;
//
//    @Autowired
//    public MethodsUtil(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
//        this.userRepository = userRepository;
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//
    public String getKeycloakUserId() {

        final KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();

        final Principal principal = (Principal) authentication.getPrincipal();

        String useIdByToken = "";

        if (principal instanceof KeycloakPrincipal) {
            KeycloakPrincipal<KeycloakSecurityContext> keycloakPrincipal =
                    (KeycloakPrincipal<KeycloakSecurityContext>) principal;
            final IDToken token = keycloakPrincipal.getKeycloakSecurityContext().getIdToken();
            return token.getSubject();
        } else {
            throw new UserNotFoundException("User not found!");
        }
    }

}
