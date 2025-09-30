package com.project.quora.service;

import com.project.quora.dto.QuestionPageResponseDTO;
import com.project.quora.dto.QuestionRequestDTO;
import com.project.quora.dto.QuestionResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IQuestionService {

    Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO);

    Mono<QuestionResponseDTO> getQuestionById(String id);

    Flux<QuestionResponseDTO> getAllQuestions();

    Mono<Void> deleteQuestionById(String id);

    Mono<QuestionPageResponseDTO> searchQuestionsByTitleContaining(String title, int page, int size);
}
