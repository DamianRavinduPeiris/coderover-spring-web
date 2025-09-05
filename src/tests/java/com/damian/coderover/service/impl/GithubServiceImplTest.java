package com.damian.coderover.service.impl;

import com.damian.coderover.dto.BranchResponse;
import com.damian.coderover.dto.BranchResponse.Commit;
import com.damian.coderover.dto.BranchResponse.Commit.CommitDetail;
import com.damian.coderover.dto.BranchResponse.Commit.CommitDetail.Tree;
import com.damian.coderover.dto.RepoDTO;
import com.damian.coderover.dto.GitTreeResponse;
import com.damian.coderover.exception.GithubException;
import com.damian.coderover.feign.GithubClient;
import com.damian.coderover.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubServiceImplTest {

    @Mock
    GithubClient githubClient;

    @InjectMocks
    GithubServiceImpl service;

    @Test
    void withBearer_validToken() {
        var header = GithubServiceImpl.withBearer("abc");
        assertThat(header).isEqualTo("Bearer abc");
    }

    @Test
    void withBearer_nullOrBlank_throws() {
        assertThrows(IllegalArgumentException.class, () -> GithubServiceImpl.withBearer(null));
        assertThrows(IllegalArgumentException.class, () -> GithubServiceImpl.withBearer(" "));
    }

    @Test
    void fetchUserRepos_filtersJavaRepos() {
        var repos = List.of(
                new RepoDTO("a","a-full","http://repo/a", false, "Java", "desc", 1, "2025-01-01"),
                new RepoDTO("b","b-full","http://repo/b", true, "Kotlin", "desc", 0, "2025-01-02")
        );
        when(githubClient.getUserRepos(anyString(), any(), any())).thenReturn(repos);

        var resp = service.fetchUserRepos("tok", 10, 1);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Response body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).contains("successfully");
        @SuppressWarnings("unchecked")
        List<RepoDTO> data = (List<RepoDTO>) body.data();
        assertThat(data).hasSize(1);
        assertThat(data.getFirst().language()).isEqualToIgnoringCase("java");
    }

    @Test
    void fetchRepoTree_success() {
        // spy to stub fetchBranchDetails
        var spy = Mockito.spy(new GithubServiceImpl(githubClient));
        var tree = new Tree("sha-tree");
        var commitDetail = new CommitDetail(tree);
        var commit = new Commit(commitDetail);
        var branchResponse = new BranchResponse("main", commit);
        var response = new Response("ok", branchResponse, HttpStatus.OK.value());
        doReturn(ResponseEntity.ok(response)).when(spy).fetchBranchDetails(anyString(), anyString(), anyString(), anyString());

        when(githubClient.getRepoTree(anyString(), anyString(), anyString(), eq("sha-tree")))
                .thenReturn(new GitTreeResponse("sha-tree", List.of()));

        var r = spy.fetchRepoTree("tok","owner","repo","main");
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().message()).contains("Repo tree fetched");
    }

    @Test
    void fetchRepoTree_missingSha_throws() {
        var spy = Mockito.spy(new GithubServiceImpl(githubClient));
        var commitDetail = new CommitDetail(null);
        var commit = new Commit(commitDetail);
        var branchResponse = new BranchResponse("main", commit);
        var response = new Response("ok", branchResponse, HttpStatus.OK.value());
        doReturn(ResponseEntity.ok(response)).when(spy).fetchBranchDetails(anyString(), anyString(), anyString(), anyString());

        var ex = assertThrows(GithubException.class, () -> spy.fetchRepoTree("tok","o","r","b"));
        assertThat(ex.getMessage()).contains("Tree SHA missing");
    }
}
