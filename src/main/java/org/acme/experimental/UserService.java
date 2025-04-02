package org.acme.experimental;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.experimental.formatter.KeycloakRepresentationFormatter;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    Keycloak keycloak;


    String getUsersFormatted(String realm){
        return KeycloakRepresentationFormatter.formatObjectList(getUsers(realm));
    }

    List<UserRepresentation> getUsers(String realm) {
        return keycloak.realm(realm).users().list();
    }

    String addUser(String realm, String username, String firstName, String lastName, String email, String password) {
        UserRepresentation user = new UserRepresentation();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEnabled(true);
        user.setEmail(email);


        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(List.of(credential));

        Response response = keycloak.realm(realm).users().create(user);
        if (response.getStatus() == 204)
            return String.format("User %s, deleted from %s realm", username, realm);
        else
            return "The system encountered an error while creating the user";
    }

    String deleteUser(String realm, String username) {
        UserRepresentation user = getUserByUsername(realm, username);
        if (user != null) {
            Response response = keycloak.realm(realm).users().delete(user.getId());
            if (response.getStatus() == 204)
                return String.format("User %s, successfully deleted from %s realm", username, realm);
           }
        return "User " + username + " not found.";
    }

    UserRepresentation getUserByUsername(String realm, String username) {
        return keycloak.realm(realm).users()
                .search(username)
                .stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst()
                .orElse(null);
    }

}

