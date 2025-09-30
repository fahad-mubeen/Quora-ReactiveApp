package com.project.quora.mapper;

import com.project.quora.dto.QuestionPageResponseDTO;
import com.project.quora.dto.QuestionRequestDTO;
import com.project.quora.dto.QuestionResponseDTO;
import com.project.quora.model.Question;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class QuestionMapper {
    public static Question toQuestion(QuestionRequestDTO questionRequestDTO) {
        return Question.builder()
                .title(questionRequestDTO.getTitle())
                .content(questionRequestDTO.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static QuestionResponseDTO toQuestionResponseDTO(Question question) {
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .createAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }

    public static Mono<QuestionPageResponseDTO> toQuestionPageResponseDTO(
            Flux<QuestionResponseDTO> questionFlux,
            Mono<Long> totalEntries
    ) {
        return questionFlux.collectList()  // Flux -> Mono<List<QuestionResponseDTO>>
                .zipWith(totalEntries)
                .map(tuple -> QuestionPageResponseDTO.builder()
                        .questionResponseDTO(tuple.getT1())
                        .totalEntries(tuple.getT2())
                        .build()
                );
    }
}
