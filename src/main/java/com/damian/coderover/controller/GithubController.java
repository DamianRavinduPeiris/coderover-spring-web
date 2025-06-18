package com.damian.coderover.controller;

import com.damian.coderover.response.Response;
import com.damian.coderover.service.GithubService;
import com.damian.coderover.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/github", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GithubController {
    private final GithubService githubService;
    private final UserService userService;

    @GetMapping(path = "/user/repos")
    public ResponseEntity<Response> fetchUserRepos(@RegisteredOAuth2AuthorizedClient("github")
                                                   OAuth2AuthorizedClient authorizedClient,
                                                   @RequestParam(value = "per_page", required = false) Integer perPage,
                                                   @RequestParam(value = "page", required = false) Integer page) {
        return githubService.fetchUserRepos(authorizedClient.getAccessToken().getTokenValue(), perPage, page);
    }

    @GetMapping("/repos/{owner}/{repo}/tree")
    public ResponseEntity<Response> fetchRepoTree(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client,
                                                  @PathVariable String owner, @PathVariable String repo,
                                                  @RequestParam(defaultValue = "master") String branch) {

        return githubService.fetchRepoTree(client.getAccessToken().getTokenValue(), owner, repo, branch);
    }

    @GetMapping("/repos/{owner}/{repo}/blob")
    public ResponseEntity<Response> getFileBlob(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client,
                                                @PathVariable String owner, @PathVariable String repo,
                                                @RequestParam String sha) {
        return githubService.fetchFileBlob(client.getAccessToken().getTokenValue(), owner, repo, sha);
    }

    @GetMapping("/repos/{owner}/{repo}")
    public ResponseEntity<Response> getAllBranches(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client,
                                                @PathVariable String owner, @PathVariable String repo) {
        return githubService.fetchAllBranches(client.getAccessToken().getTokenValue(), owner, repo);
    }

    @GetMapping( "/user")
    public ResponseEntity<Response> fetchUserInfo() {
        return userService.fetchUserInfo();
    }
}
