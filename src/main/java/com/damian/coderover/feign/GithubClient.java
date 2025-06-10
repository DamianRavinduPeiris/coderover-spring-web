package com.damian.coderover.feign;

import com.damian.coderover.dto.RepoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "GithubClient", url = "${github.base-uri}")
public interface GithubClient {
    @GetMapping(value = "/user/repos", headers = "Accept=application/vnd.github+json")
    List<RepoDTO> getUserRepos(@RequestHeader("Authorization") String authHeader);
}
