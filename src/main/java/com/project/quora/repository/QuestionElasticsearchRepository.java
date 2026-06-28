package com.project.quora.repository;

import com.project.quora.model.QuestionDocument;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionElasticsearchRepository extends ReactiveElasticsearchRepository<QuestionDocument, String> {
}
