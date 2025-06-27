package com.damian.coderover.service.impl;

import com.damian.coderover.dto.BranchResponse;
import com.damian.coderover.exception.GithubException;
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

    private static final String JAVA = "Java";
    private static final String SUCCESS = "successfully";
    private static final String ERROR_BRANCH_NOT_FOUND = "Branch not found!";
    private static final String ERROR_NULL_BRANCH_SHA = "Tree SHA missing in branch commit.";

    private final GithubClient githubClient;

    public static String withBearer(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("Access token cannot be null or blank");
        }
        return "Bearer " + accessToken;
    }

    @Override
    public ResponseEntity<Response> fetchUserRepos(String accessToken, Integer perPage, Integer page) {
        try {
            var authHeader = withBearer(accessToken);
            var repos = githubClient.getUserRepos(authHeader, perPage, page);
            var javaRepos = repos.stream()
                .filter(repoDTO -> repoDTO.language() != null && repoDTO.language().equalsIgnoreCase(JAVA))
                .toList();
            return ResponseEntity.ok(new Response("User repositories fetched successfully", javaRepos, HttpStatus.OK.value()));
        } catch (Exception e) {
            throw new GithubException("Failed to fetch user repositories: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Response> fetchRepoTree(String accessToken, String owner, String repo, String branch) {
        try {
            var branchDetailsResponse = fetchBranchDetails(accessToken, owner, repo, branch);
            var body = branchDetailsResponse.getBody();

            if (branchDetailsResponse.getStatusCode().is2xxSuccessful() && body != null) {
                    var branchData = (BranchResponse) body.data();
                    if (branchData == null || branchData.commit() == null || branchData.commit().commit() == null || branchData.commit().commit().tree() == null) {
                        throw new GithubException(ERROR_NULL_BRANCH_SHA);
                    }

                    var sha = branchData.commit().commit().tree().sha();
                    var authHeader = withBearer(accessToken);
                    var repoTree = githubClient.getRepoTree(authHeader, owner, repo, sha);

                    return ResponseEntity.ok(new Response("Repo tree fetched " + SUCCESS, repoTree, HttpStatus.OK.value()));
            } else {
                throw new GithubException(ERROR_BRANCH_NOT_FOUND);
            }

        } catch (Exception e) {
            throw new GithubException("Failed to fetch repo tree: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Response> fetchFileBlob(String accessToken, String owner, String repo, String sha) {
        try {
            var authHeader = withBearer(accessToken);
            var fileBlob = githubClient.getFileBlob(authHeader, owner, repo, sha);
            return ResponseEntity.ok(new Response("File blob fetched " + SUCCESS, fileBlob, HttpStatus.OK.value()));
        } catch (Exception e) {
            throw new GithubException("Failed to fetch file blob: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Response> fetchBranchDetails(String accessToken, String owner, String repo, String branch) {
        try {
            var authHeader = withBearer(accessToken);
            var branchDetails = githubClient.getBranchDetails(authHeader, owner, repo, branch);
            return ResponseEntity.ok(new Response("Branch details fetched " + SUCCESS, branchDetails, HttpStatus.OK.value()));
        } catch (Exception e) {
            throw new GithubException("Failed to fetch branch details: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Response> fetchAllBranches(String accessToken, String owner, String repo) {
        try {
            var authHeader = withBearer(accessToken);
            var branchDetails = githubClient.getAllBranches(authHeader, owner, repo);
            return ResponseEntity.ok(new Response("All branches fetched :  " + SUCCESS, branchDetails, HttpStatus.OK.value()));
        } catch (Exception e) {
            throw new GithubException("Failed to fetch branches : " + e.getMessage());
        }
    }
}
