package com.project.quora.repository;

import com.project.quora.model.Question;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface QuestionRepository extends ReactiveMongoRepository<Question, String> {
    Flux<Question> findByTitleIsContainingIgnoreCase(String title);

    Mono<Long> countByTitleContainingIgnoreCase(String title);

//    Flux<Question> findByAuthorId(String authorId);

//    Mono<Long> countByAuthorId(String authorId);

}
