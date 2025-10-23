package com.project.quora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CursorPaginatedResponse<T> {
    private List<T> data;
    private CursorPaginationMeta pagination;
}