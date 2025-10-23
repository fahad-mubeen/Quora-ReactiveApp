package com.project.quora.controller;

import com.project.quora.dto.AnswerRequestDTO;
import com.project.quora.dto.AnswerResponseDTO;
import com.project.quora.service.IAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final IAnswerService answerService;

    @PostMapping
    public Mono<AnswerResponseDTO> createAnswer(@RequestBody AnswerRequestDTO answerRequestDTO) {
        return answerService.createAnswer(answerRequestDTO);
    }

    @PutMapping
    public Mono<AnswerResponseDTO> updateAnswer(@RequestBody AnswerRequestDTO answerRequestDTO) {
        return answerService.updateAnswer(answerRequestDTO);
    }

    @DeleteMapping("/{answerId}")
    public Mono<Void> deleteAnswer(@PathVariable String answerId) {
        return answerService.deleteAnswer(answerId);
    }

    @GetMapping
    public Flux<AnswerResponseDTO> getAllAnswers() {
        return answerService.getAllAnswers();
    }

    @GetMapping("/question/{questionId}")
    public Flux<AnswerResponseDTO> getAnswersByQuestionId(@PathVariable String questionId) {
        return answerService.getAnswersByQuestionId(questionId);
    }

    @GetMapping("/{answerId}")
    public Mono<AnswerResponseDTO> getAnswerById(@PathVariable String answerId) {
        return answerService.getAnswerById(answerId);
    }
}
