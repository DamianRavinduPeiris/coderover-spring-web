package com.damian.coderover.service;

import com.damian.coderover.response.Response;
import org.springframework.http.ResponseEntity;

public interface GithubService {
    ResponseEntity<Response> fetchUserRepos(String accessToken, Integer perPage, Integer page);

    ResponseEntity<Response> fetchRepoTree(String accessToken, String owner, String repo, String branch);

    ResponseEntity<Response> fetchFileBlob(String accessToken, String owner, String repo, String sha);

    ResponseEntity<Response> fetchBranchDetails(String accessToken, String owner, String repo, String branch);

    ResponseEntity<Response> fetchAllBranches(String accessToken, String owner, String repo);
}
