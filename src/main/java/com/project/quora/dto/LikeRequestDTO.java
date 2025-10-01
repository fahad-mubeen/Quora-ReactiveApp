package com.project.quora.dto;

import com.project.quora.enums.LikeFor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LikeRequestDTO {

    private String LikeForId;

    private LikeFor likeFor; // Enum to specify if it's for QUESTION, ANSWER, or COMMENT

    boolean isUpvoted; // true for upvote, false for downvote
}
