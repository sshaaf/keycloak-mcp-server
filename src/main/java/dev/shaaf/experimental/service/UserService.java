package dev.shaaf.experimental.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import io.quarkus.logging.Log;

import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    Keycloak keycloak;

    public List<UserRepresentation> getUsers(String realm) {
        return keycloak.realm(realm).users().list();

    }

    public String addUser(String realm, String username, String firstName, String lastName, String email, String password) {
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
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            return "Successfully created user: " + username;
        } else {
            Log.error("Failed to create user. Status: " + response.getStatus());
            response.close();
            return "Error creating user: "+" "+username;
        }
    }

    public String deleteUser(String realm, String username) {
        UserRepresentation user = getUserByUsername(realm, username);
        if (user != null) {
            Response response = keycloak.realm(realm).users().delete(user.getId());
            if(response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
                return "successfully deleted: "+user.getId();
            else
                return "failed to delete: "+user.getId();
        }
        else
            return "User not found: "+user.getId();
    }


    public UserRepresentation getUserByUsername(String realm, String username) {
        return keycloak.realm(realm).users()
                .search(username)
                .stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst()
                .orElse(null);
    }

}

