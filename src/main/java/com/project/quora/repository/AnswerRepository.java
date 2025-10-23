package com.project.quora.repository;

import com.project.quora.model.Answer;
import com.project.quora.model.Question;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Repository
public interface AnswerRepository extends ReactiveMongoRepository<Answer, String> {

    Flux<Answer> findByQuestionId(String questionId);
}
