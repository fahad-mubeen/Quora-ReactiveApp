package com.project.quora.service;

import com.project.quora.dto.QuestionResponseDTO;
import reactor.core.publisher.Flux;

public interface IElasticSearchService {
    Flux<QuestionResponseDTO> searchQuestions(String query);
}
