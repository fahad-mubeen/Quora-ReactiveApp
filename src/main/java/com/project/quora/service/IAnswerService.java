package com.project.quora.service;

import com.project.quora.dto.AnswerRequestDTO;
import com.project.quora.dto.AnswerResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAnswerService {

    Mono<AnswerResponseDTO> createAnswer(AnswerRequestDTO answerRequestDTO);

    Mono<AnswerResponseDTO> updateAnswer(AnswerRequestDTO answerRequestDTO);

    Mono<Void> deleteAnswer(String answerId);

    Flux<AnswerResponseDTO> getAllAnswers();

    Flux<AnswerResponseDTO> getAnswersByQuestionId(String questionId);

    Mono<AnswerResponseDTO> getAnswerById(String answerId);
}
