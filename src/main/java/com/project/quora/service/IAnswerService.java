package com.project.quora.service;

import com.project.quora.dto.AnswerRequestDTO;
import com.project.quora.dto.AnswerResponseDTO;
import reactor.core.publisher.Mono;

public interface IAnswerService {

    Mono<AnswerResponseDTO> createAnswer(AnswerRequestDTO answerRequestDTO);

    Mono<AnswerResponseDTO> deleteAnswer(AnswerRequestDTO answerRequestDTO);

    Mono<AnswerResponseDTO> getAnswerById(AnswerRequestDTO answerRequestDTO);

    Mono<AnswerResponseDTO> getAllAnswers(AnswerRequestDTO answerRequestDTO);
}
