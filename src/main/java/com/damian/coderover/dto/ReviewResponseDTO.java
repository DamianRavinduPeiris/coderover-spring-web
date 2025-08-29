package com.damian.coderover.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class ReviewResponseDTO implements Serializable {
    private String id;
    private String provider;
    private String model;
    private String object;
    private long created;
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Object logprobs;
        private String finish_reason;
        private String native_finish_reason;
        private int index;
        private Message message;

        @Data
        public static class Message {
            private String role;
            private String content;
        }
    }
}