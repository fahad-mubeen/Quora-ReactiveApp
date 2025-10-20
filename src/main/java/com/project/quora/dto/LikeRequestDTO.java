package com.project.quora.dto;

import com.project.quora.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LikeRequestDTO {

    private String LikeForId;

    private TargetType targetType; // Enum to specify if it's for QUESTION, ANSWER, or COMMENT

    boolean upvoted; // true for upvote, false for downvote
}
