package com.project.quora.service;

import com.project.quora.dto.AnswerRequestDTO;
import com.project.quora.dto.AnswerResponseDTO;
import com.project.quora.dto.PaginatedResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAnswerService {

    Mono<AnswerResponseDTO> createAnswer(AnswerRequestDTO answerRequestDTO);

    Mono<AnswerResponseDTO> updateAnswer(AnswerRequestDTO answerRequestDTO);

    Mono<Void> deleteAnswer(String answerId);

    Mono<PaginatedResponse<AnswerResponseDTO>> getAllAnswers(int page, int size);

    Mono<PaginatedResponse<AnswerResponseDTO>> getAnswersByQuestionId(String questionId, int page, int size);

    Mono<AnswerResponseDTO> getAnswerById(String answerId);
}
