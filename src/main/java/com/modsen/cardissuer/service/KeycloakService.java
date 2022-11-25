package com.modsen.cardissuer.service;

import com.modsen.cardissuer.configuration.KeycloakManager;
import com.modsen.cardissuer.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakManager keycloakManager;

    public Integer createUser(UserRepresentation userRepresentation) {
        final Response response = keycloakManager.getKeycloakInstanceWithRealm().users().create(userRepresentation);
        return response.getStatus();
    }

    public void updateUser(UserRepresentation userRepresentation) {
        keycloakManager.getKeycloakInstanceWithRealm().users().get(userRepresentation.getId()).update(userRepresentation);
    }

    public UserRepresentation readUser(String keycloakUserId) {
        try {
            final UserResource userResource = keycloakManager.getKeycloakInstanceWithRealm().users().get(keycloakUserId);
            return userResource.toRepresentation();
        } catch (Exception e) {
            throw new UserNotFoundException("user not found under given ID");
        }
    }
}
