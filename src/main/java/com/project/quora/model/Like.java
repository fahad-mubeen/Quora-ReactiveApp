package com.project.quora.model;
import com.project.quora.enums.TargetType;
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
    private String targetId; // ID of the QUESTION, ANSWER, or COMMENT being liked/disliked

    private TargetType targetType; // Enum to specify if it's for QUESTION, ANSWER, or COMMENT

    boolean upvoted; // true for upvote, false for downvote

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
