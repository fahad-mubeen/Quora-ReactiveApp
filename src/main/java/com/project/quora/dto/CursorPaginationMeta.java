package com.project.quora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CursorPaginationMeta {
    private String nextCursor;
    private boolean hasMore;
    private int pageSize;
}