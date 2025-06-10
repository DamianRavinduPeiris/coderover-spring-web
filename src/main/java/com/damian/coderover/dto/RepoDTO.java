package com.damian.coderover.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RepoDTO(
        String name,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("private") boolean isPrivate,
        String language
) {}