package dev.shaaf.keycloak.mcp.server.discourse.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Topic(
        int id,
        String title,
        String slug,
        @JsonProperty("posts_count") int postsCount
) {
}
