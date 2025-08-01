package dev.shaaf.keycloak.mcp.server.discourse.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Post(
        int id,
        String name,
        String username,
        @JsonProperty("topic_id") int topicId,
        String blurb
) {
}
