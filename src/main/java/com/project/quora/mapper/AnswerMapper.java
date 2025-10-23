package com.project.quora.mapper;

import com.project.quora.dto.AnswerRequestDTO;
import com.project.quora.dto.AnswerResponseDTO;
import com.project.quora.model.Answer;

import java.time.LocalDateTime;

public class AnswerMapper {
    public static Answer toAnswer(AnswerRequestDTO answerRequestDTO) {
        return Answer.builder()
                .questionId(answerRequestDTO.getQuestionId())
                .title(answerRequestDTO.getTitle())
                .content(answerRequestDTO.getContent())
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static AnswerResponseDTO toAnswerResponseDTO(Answer answer) {
        return AnswerResponseDTO.builder()
                .id(answer.getId())
                .questionId(answer.getQuestionId())
                .title(answer.getTitle())
                .content(answer.getContent())
                .viewCount(answer.getViewCount())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }
}
