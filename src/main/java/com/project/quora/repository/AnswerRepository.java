package com.project.quora.repository;

import com.project.quora.model.Answer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AnswerRepository extends ReactiveMongoRepository<Answer, String> {

    Flux<Answer> findByQuestionId(String questionId, Pageable pageable);

    @Query("{}")
    Flux<Answer> findAll(Pageable pageable);

    Mono<Long> countByQuestionId(String questionId);
}
