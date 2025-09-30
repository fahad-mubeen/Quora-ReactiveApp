package com.project.quora.controller;

import com.project.quora.dto.QuestionPageResponseDTO;
import com.project.quora.dto.QuestionRequestDTO;
import com.project.quora.dto.QuestionResponseDTO;
import com.project.quora.service.IQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final IQuestionService questionService;

    @GetMapping("/{id}")
    Mono<QuestionResponseDTO> getQuestionById(@PathVariable String id) {
        return questionService.getQuestionById(id);
    }

    @GetMapping
    Flux<QuestionResponseDTO> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping(params = {"size"})
    Flux<QuestionResponseDTO> getPaginatedQuestions(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "2") int size
    ) {
        return questionService.getPaginatedQuestions(cursor, size);
    }

    @PostMapping
    public Mono<QuestionResponseDTO> createQuestion(@RequestBody QuestionRequestDTO questionRequestDTO) {
        return questionService.createQuestion(questionRequestDTO)
                .doOnSuccess(res -> System.out.println("Question created with ID: " + res.getId()))
                .doOnError(err -> System.err.println("Error creating question: " + err.getMessage()));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteQuestion(@PathVariable String id) {
        return questionService.deleteQuestionById(id);
    }

    @GetMapping("/search")
    public Mono<QuestionPageResponseDTO> searchQuestionsByTitleContaining(
            @RequestParam(defaultValue = "Sample") String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        return questionService.searchQuestionsByTitleContaining(title, page, size);
    }
}
