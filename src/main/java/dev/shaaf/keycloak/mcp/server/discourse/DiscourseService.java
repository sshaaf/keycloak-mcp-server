package dev.shaaf.keycloak.mcp.server.discourse;

import dev.shaaf.keycloak.mcp.server.discourse.model.SearchResult;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/search.json")
@RegisterRestClient(baseUri = "https://keycloak.discourse.group")
public interface DiscourseService {

    @GET
    SearchResult search(@QueryParam("q") String query);
}
