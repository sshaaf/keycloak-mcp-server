package dev.shaaf.experimental;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    Keycloak keycloak;

    @Inject
    Jsonb jsonb;

    String getUsers(String realm) {
        return jsonb.toJson(keycloak.realm(realm).users().list());
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
        System.out.println(keycloak.realm(realm).users().create(user).toString());
        System.out.println(jsonb.toJson(user));
        return jsonb.toJson(user);
    }

    String deleteUser(String realm, String username) {
        UserRepresentation user = getUserByUsername(realm, username);
        if (user != null) {
            return jsonb.toJson(keycloak.realm(realm).users().delete(user.getId()));
        }
        else
            return "User not found";
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

