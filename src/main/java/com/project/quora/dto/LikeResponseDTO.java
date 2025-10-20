package com.project.quora.dto;

import com.project.quora.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class LikeResponseDTO {

    private String id;

    private String LikeForId;

    private TargetType targetType; // Enum to specify if it's for QUESTION, ANSWER, or COMMENT

    boolean upvoted; // true for upvote, false for downvote

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
