package com.project.quora.model;
import com.fasterxml.jackson.databind.annotation.EnumNaming;
import com.project.quora.enums.LikeFor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@Document(collection = "likes")
public class Like {
    @Id
    private String id;

    @Indexed
    private String LikeForId;

    private LikeFor likeFor; // Enum to specify if it's for QUESTION, ANSWER, or COMMENT

    boolean isUpvoted; // true for upvote, false for downvote

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
