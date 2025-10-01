package com.project.quora.repository;

import com.project.quora.model.Answer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AnswerRepository extends ReactiveMongoRepository<Answer, String> {

}
