package dev.shaaf.experimental.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.representations.idm.ClientRepresentation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClientService {

    @Inject
    Keycloak keycloak;

    public List<ClientRepresentation> getClients(String realm) {
        return keycloak.realm(realm).clients().findAll();
    }

    public Optional<ClientRepresentation> findClientByClientId(String realm, String clientId) {
        List<ClientRepresentation> clients = keycloak.realm(realm).clients().findByClientId(clientId);
        if (clients != null && !clients.isEmpty()) {
            return Optional.of(clients.get(0));
        }
        return Optional.empty();
    }

    public String createClient(String realm, String clientName) {
        ClientsResource clientsResource = keycloak.realm(realm).clients();
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(clientName);
        clientRepresentation.setName(clientName);
        clientRepresentation.setProtocol("openid-connect");

        clientRepresentation.setPublicClient(false);
        clientRepresentation.setStandardFlowEnabled(true);
        clientRepresentation.setDirectAccessGrantsEnabled(false);
        clientRepresentation.setServiceAccountsEnabled(false);
        clientRepresentation.setEnabled(true);
        clientRepresentation.setRedirectUris(Collections.singletonList("http://localhost:8080/redirect/*"));


        Response response = clientsResource.create(clientRepresentation);
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            return "Successfully created client: " + clientName;
        } else {
            Log.error("Failed to create client. Status: " + response.getStatus());
            response.close();
            return "Error creating cliet: "+clientName;
        }
    }

}
