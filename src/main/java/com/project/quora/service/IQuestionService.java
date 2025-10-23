package com.project.quora.service;

import com.project.quora.dto.CursorPaginatedResponse;
import com.project.quora.dto.PaginatedResponse;
import com.project.quora.dto.QuestionRequestDTO;
import com.project.quora.dto.QuestionResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IQuestionService {

    Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO);

    Mono<QuestionResponseDTO> getQuestionById(String id);

    Flux<QuestionResponseDTO> getAllQuestions();

    Mono<Void> deleteQuestionById(String id);

    Mono<PaginatedResponse<QuestionResponseDTO>> searchQuestionsByTitleContaining(String title, int page, int size);

    Mono<CursorPaginatedResponse<QuestionResponseDTO>> getPaginatedQuestions(String cursor, int size);
}
