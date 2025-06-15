package com.damian.coderover.dto;

public record BranchResponse(
        String name,
        Commit commit
) {
    public record Commit(
            CommitDetail commit
    ) {
        public record CommitDetail(
                Tree tree
        ) {
            public record Tree(
                    String sha
            ) {}
        }
    }
}
