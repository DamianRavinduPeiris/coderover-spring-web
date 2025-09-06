package com.damian.coderover.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewResponseDTO implements Serializable {

    private String id;
    private String model;
    private String object;

    @JsonProperty("output")
    private List<Output> outputs;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output {
        private String id;
        private String type;

        @JsonProperty("content")
        private List<Content> contents;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private String type;
        private String text;
    }
}
