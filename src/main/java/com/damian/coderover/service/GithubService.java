package com.damian.coderover.service;

import com.damian.coderover.response.Response;
import org.springframework.http.ResponseEntity;

public interface GithubService {
    ResponseEntity<Response> fetchUserRepos(String accessToken);
}
