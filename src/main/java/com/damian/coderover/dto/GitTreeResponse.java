package com.damian.coderover.dto;

import java.util.List;

public record GitTreeResponse(String sha, List<TreeItem> tree) {
    public record TreeItem(String path, String type, String sha) {}
}
