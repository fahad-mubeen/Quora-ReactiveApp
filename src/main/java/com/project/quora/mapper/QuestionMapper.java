package com.project.quora.mapper;

import com.project.quora.dto.QuestionRequestDTO;
import com.project.quora.dto.QuestionResponseDTO;
import com.project.quora.model.Question;
import com.project.quora.model.QuestionDocument;

import java.time.LocalDateTime;

public class QuestionMapper {
    public static Question toQuestion(QuestionRequestDTO questionRequestDTO) {
        return Question.builder()
                .title(questionRequestDTO.getTitle())
                .content(questionRequestDTO.getContent())
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static QuestionResponseDTO toQuestionResponseDTO(Question question) {
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .viewCount(question.getViewCount())
                .createAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }

    public static QuestionDocument toQuestionDocument(Question question) {
        if (question == null) {
            return null;
        }
        return QuestionDocument.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .viewCount(question.getViewCount())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }

    public static Question toQuestion(QuestionDocument questionDocument) {
        if (questionDocument == null) {
            return null;
        }
        return Question.builder()
                .id(questionDocument.getId())
                .title(questionDocument.getTitle())
                .content(questionDocument.getContent())
                .viewCount(questionDocument.getViewCount())
                .createdAt(questionDocument.getCreatedAt())
                .updatedAt(questionDocument.getUpdatedAt())
                .build();
    }

    public static QuestionResponseDTO toQuestionResponseDTO(QuestionDocument questionDocument) {
        if (questionDocument == null) {
            return null;
        }
        return QuestionResponseDTO.builder()
                .id(questionDocument.getId())
                .title(questionDocument.getTitle())
                .content(questionDocument.getContent())
                .viewCount(questionDocument.getViewCount())
                .createAt(questionDocument.getCreatedAt())
                .updatedAt(questionDocument.getUpdatedAt())
                .build();
    }
}
