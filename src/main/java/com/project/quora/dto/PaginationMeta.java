package com.project.quora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PaginationMeta {
    private long totalItems;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private String nextPageUrl;
}