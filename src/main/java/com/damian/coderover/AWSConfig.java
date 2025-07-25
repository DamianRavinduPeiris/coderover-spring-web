package com.damian.coderover;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codegurureviewer.CodeGuruReviewerClient;

@Configuration
public class AWSConfig {
    @Bean
    public CodeGuruReviewerClient codeGuruReviewerClient() {
        return CodeGuruReviewerClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider.builder().build())
                .build();
    }
}
