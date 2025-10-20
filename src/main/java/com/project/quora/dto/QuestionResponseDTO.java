package com.project.quora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class QuestionResponseDTO {

    private String id;

    private String title;

    private String content;

    private Integer viewCount;

    private LocalDateTime createAt;

    private LocalDateTime updatedAt;
}
