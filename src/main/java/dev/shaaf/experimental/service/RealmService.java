package dev.shaaf.experimental.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;

import java.util.List;


@ApplicationScoped
public class RealmService {

    @Inject
    Keycloak keycloak;

    public List<RealmRepresentation> getRealms() {
        return keycloak.realms().findAll();
    }


}
