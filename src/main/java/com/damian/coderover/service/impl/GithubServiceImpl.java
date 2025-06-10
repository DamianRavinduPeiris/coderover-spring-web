package com.damian.coderover.service.impl;

import com.damian.coderover.feign.GithubClient;
import com.damian.coderover.response.Response;
import com.damian.coderover.service.GithubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class GithubServiceImpl implements GithubService {
    private final GithubClient githubClient;

    @Override
    public ResponseEntity<Response> fetchUserRepos(String accessToken) {
        try {
            var repos = githubClient.getUserRepos("Bearer " + accessToken);
            log.info("Fetched {} repositories for user", repos.size());
            repos = repos.stream()
                    .filter(repo -> "Java".equalsIgnoreCase(repo.language()))
                    .toList();
            return ResponseEntity.ok(new Response("User repositories fetched successfully", repos, HttpStatus.OK.value()));
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user repositories: " + e.getMessage(), e);
        }
    }
}
