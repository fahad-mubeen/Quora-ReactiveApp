package com.project.quora.event;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerCreatedEvent {
    private String answerId;
    private String questionId;
    private String answerAuthorId;
    private String questionAuthorId;
    private String answerText;
    private LocalDateTime timestamp;
}