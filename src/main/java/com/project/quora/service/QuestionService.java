package com.project.quora.service;


import com.project.quora.dto.QuestionRequestDTO;
import com.project.quora.dto.QuestionResponseDTO;
import com.project.quora.mapper.QuestionMapper;
import com.project.quora.model.Question;
import com.project.quora.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

}
