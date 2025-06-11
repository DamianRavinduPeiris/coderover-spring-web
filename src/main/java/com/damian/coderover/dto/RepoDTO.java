package com.damian.coderover.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RepoDTO(
        String name,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("private") boolean isPrivate,
        String language,
        String description,
        @JsonProperty("stargazers_count") int starredCount,
        @JsonProperty("updated_at") String lastUpdatedAt
) {}