package dev.shaaf.keycloak.mcp.server.discourse.model;

import java.util.List;

public record SearchResult(List<Post> posts, List<Topic> topics) {
}