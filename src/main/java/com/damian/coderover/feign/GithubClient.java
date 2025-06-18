package com.damian.coderover.feign;

import com.damian.coderover.dto.BranchResponse;
import com.damian.coderover.dto.GitBlobResponse;
import com.damian.coderover.dto.GitTreeResponse;
import com.damian.coderover.dto.RepoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "GithubClient", url = "${github.base-uri}")
public interface GithubClient {

    @GetMapping(value = "/user/repos", headers = "Accept=application/vnd.github+json")
    List<RepoDTO> getUserRepos(
            @RequestHeader("Authorization") String authHeader,
            @org.springframework.web.bind.annotation.RequestParam(value = "per_page", required = false) Integer perPage,
            @org.springframework.web.bind.annotation.RequestParam(value = "page", required = false) Integer page
    );

    @GetMapping("/repos/{owner}/{repo}/git/trees/{sha}?recursive=1")
    GitTreeResponse getRepoTree(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable String sha
    );

    @GetMapping(
            value = "/repos/{owner}/{repo}/git/blobs/{sha}",
            headers = "Accept: application/vnd.github+json"
    )
    GitBlobResponse getFileBlob(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @PathVariable("sha") String sha
    );

    @GetMapping("/repos/{owner}/{repo}/branches/{branch}")
    BranchResponse getBranchDetails(
            @RequestHeader("Authorization") String token,
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable String branch
    );

    @GetMapping("/repos/{owner}/{repo}/branches")
    List<BranchResponse> getAllBranches(
            @RequestHeader("Authorization") String token,
            @PathVariable String owner,
            @PathVariable String repo
    );


}
