package com.project.quora.service;

import com.project.quora.dto.QuestionResponseDTO;
import com.project.quora.mapper.QuestionMapper;
import com.project.quora.model.QuestionDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ElasticSearchService implements IElasticSearchService {

    private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;

    @Override
    public Flux<QuestionResponseDTO> searchQuestions(String queryText) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .fields("title^2.0", "content")
                                .query(queryText)
                                .fuzziness("AUTO")
                        )
                )
                .build();

        return reactiveElasticsearchOperations.search(query, QuestionDocument.class, IndexCoordinates.of("questions"))
                .map(SearchHit::getContent)
                .map(QuestionMapper::toQuestionResponseDTO);
    }
}
