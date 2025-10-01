package com.project.quora.repository;

import com.project.quora.dto.QuestionResponseDTO;
import com.project.quora.model.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface QuestionRepository extends ReactiveMongoRepository<Question, String> {

    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'content': { $regex: ?0, $options: 'i' } } ] }")
    Flux<Question> findByTitleOrContentContainingIgnoreCase(String searchTerm);

    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    Flux<Question> findByTitleContainingIgnoreCase(String searchTerm, Pageable pageable);

    Mono<Long> countByTitleContainingIgnoreCase(String title);

    Flux<Question> findByCreatedAtGreaterThanOrderByCreatedAtDesc(LocalDateTime localDateTime, Pageable pageable);

    Flux<Question> findTop10ByOrderByCreatedAtDesc();
}
