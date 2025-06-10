package com.damian.coderover.controller;

import com.damian.coderover.response.Response;
import com.damian.coderover.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/github", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GithubController {
    private final GithubService githubService;

    @GetMapping(path = "/user/repos")
    public ResponseEntity<Response> fetchUserRepos(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient) {
        return githubService.fetchUserRepos(authorizedClient.getAccessToken().getTokenValue());
    }
}
