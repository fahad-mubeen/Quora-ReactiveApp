package com.project.quora.service;


import com.project.quora.dto.QuestionPageResponseDTO;
import com.project.quora.dto.QuestionRequestDTO;
import com.project.quora.dto.QuestionResponseDTO;
import com.project.quora.mapper.QuestionMapper;
import com.project.quora.model.Question;
import com.project.quora.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO) {
        Question question = QuestionMapper.toQuestion(questionRequestDTO);

        Mono<Question> saved = questionRepository.save(question);
        Mono<QuestionResponseDTO> response = saved.map(q -> QuestionMapper.toQuestionResponseDTO(q));

        return response
                .doOnSuccess(res -> {
                    System.out.println("Question created with ID: " + res.getId());
                })
                .doOnError(err -> {
                    System.err.println("Error creating question: " + err.getMessage());
                });
    }

    @Override
    public Mono<QuestionResponseDTO> getQuestionById(String id) {
        Mono<Question> question = questionRepository.findById(id);
        return question.map(QuestionMapper::toQuestionResponseDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Question not found")));
    }

    @Override
    public Flux<QuestionResponseDTO> getAllQuestions() {
        return questionRepository.findAll().map(QuestionMapper::toQuestionResponseDTO);
    }

    @Override
    public Mono<Void> deleteQuestionById(String id) {
        return questionRepository.deleteById(id).doOnSuccess(res -> {
            System.out.println("Question deleted with ID: " + id);
        }).doOnError(err -> {
            System.err.println("Error deleting question: " + err.getMessage());
        });
    }

    @Override
    public Mono<QuestionPageResponseDTO> searchQuestionsByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Flux<QuestionResponseDTO> questionFlux = questionRepository
                .findByTitleIsContainingIgnoreCase(title)
                .skip(pageable.getOffset()) // page * size
                .take(size)
                .map(QuestionMapper::toQuestionResponseDTO);

        Mono<Long> totalCount = questionRepository.countByTitleContainingIgnoreCase(title);
        return QuestionMapper.toQuestionPageResponseDTO(questionFlux, totalCount);
    }

}
