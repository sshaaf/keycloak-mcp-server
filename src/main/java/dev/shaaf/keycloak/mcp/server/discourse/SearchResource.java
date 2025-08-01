package dev.shaaf.keycloak.mcp.server.discourse;

import dev.shaaf.keycloak.mcp.server.discourse.model.SearchResult;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/search")
public class SearchResource {

    @RestClient
    DiscourseService discourseService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult performSearch(@QueryParam("term") String term) {
        return discourseService.search(term);
    }
}
