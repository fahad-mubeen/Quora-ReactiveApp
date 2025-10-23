package com.project.quora.controller;

import com.project.quora.dto.AnswerRequestDTO;
import com.project.quora.dto.AnswerResponseDTO;
import com.project.quora.dto.PaginatedResponse;
import com.project.quora.service.IAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    public Mono<PaginatedResponse<AnswerResponseDTO>> getAllAnswers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return answerService.getAllAnswers(page, size);
    }

    @GetMapping("/question/{questionId}")
    public Mono<PaginatedResponse<AnswerResponseDTO>> getAnswersByQuestionId(
            @PathVariable String questionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return answerService.getAnswersByQuestionId(questionId, page, size);
    }

    @GetMapping("/{answerId}")
    public Mono<AnswerResponseDTO> getAnswerById(@PathVariable String answerId) {
        return answerService.getAnswerById(answerId);
    }
}
