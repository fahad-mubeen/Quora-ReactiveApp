package com.project.quora.service;

import com.project.quora.dto.AnswerRequestDTO;
import com.project.quora.dto.AnswerResponseDTO;
import com.project.quora.mapper.AnswerMapper;
import com.project.quora.model.Answer;
import com.project.quora.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnswerService implements IAnswerService {

    private final AnswerRepository answerRepository;

    @Override
    public Mono<AnswerResponseDTO> createAnswer(AnswerRequestDTO answerRequestDTO) {
        Answer answer = AnswerMapper.toAnswer(answerRequestDTO);
        return answerRepository.save(answer).map(AnswerMapper::toAnswerResponseDTO);
    }

    @Override
    public Mono<AnswerResponseDTO> updateAnswer(AnswerRequestDTO answerRequestDTO) {
        if (answerRequestDTO.getId() == null || answerRequestDTO.getId().isBlank()) {
            return Mono.error(new IllegalArgumentException("No valid ID provided for update"));
        }

        return answerRepository.findById(answerRequestDTO.getId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Answer with ID not found")))
                .flatMap(existing -> {
                    existing.setTitle(answerRequestDTO.getTitle());
                    existing.setContent(answerRequestDTO.getContent());
                    existing.setQuestionId(answerRequestDTO.getQuestionId());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return answerRepository.save(existing);
                })
                .map(AnswerMapper::toAnswerResponseDTO);
    }

    @Override
    public Mono<Void> deleteAnswer(String answerId) {
        return answerRepository.deleteById(answerId);
    }

    @Override
    public Flux<AnswerResponseDTO> getAllAnswers() {
        Flux<Answer> answers = answerRepository.findAll();
        return answers.map(AnswerMapper::toAnswerResponseDTO);
    }

    @Override
    public Flux<AnswerResponseDTO> getAnswersByQuestionId(String questionId) {
        Flux<Answer> answers = answerRepository.findByQuestionId(questionId);
        return answers.map(AnswerMapper::toAnswerResponseDTO);
    }

    @Override
    public Mono<AnswerResponseDTO> getAnswerById(String answerId) {
        return answerRepository.findById(answerId)
                .map(AnswerMapper::toAnswerResponseDTO);
    }
}

