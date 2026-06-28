package com.project.quora.service;

import com.project.quora.dto.*;
import com.project.quora.enums.TargetType;
import com.project.quora.event.QuestionUpdateEvent;
import com.project.quora.event.ViewCountEvent;
import com.project.quora.mapper.QuestionMapper;
import com.project.quora.model.Question;
import com.project.quora.producer.KafkaEventProducer;
import com.project.quora.repository.QuestionRepository;
import com.project.quora.utils.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;

    private final KafkaEventProducer kafkaEventProducer;

    @Override
    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO) {
        Question question = QuestionMapper.toQuestion(questionRequestDTO);

        Mono<Question> saved = questionRepository.save(question);
        Mono<QuestionResponseDTO> response = saved.map(QuestionMapper::toQuestionResponseDTO);

        return response
                .doOnSuccess(res -> {
                    System.out.println("Question created with ID: " + res.getId());
                    kafkaEventProducer.publishQuestionUpdateEvent(
                            QuestionUpdateEvent.builder().questionId(res.getId()).build()
                    );
                })
                .doOnError(err -> {
                    System.err.println("Error creating question: " + err.getMessage());
                });
    }

    @Override
    public Mono<QuestionResponseDTO> getQuestionById(String questionId) {
        return questionRepository.findById(questionId)
                .map(QuestionMapper::toQuestionResponseDTO)
                .doOnError(err -> System.out.println("Error fetching question by ID: " + err.getMessage()))
                .doOnSuccess(res -> {
                    kafkaEventProducer.publishViewCountEvent(
                            ViewCountEvent.builder()
                                    .targetId(questionId)
                                    .targetType(TargetType.QUESTION)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    @Override
    public Flux<QuestionResponseDTO> getAllQuestions() {
        return questionRepository.findAll().map(QuestionMapper::toQuestionResponseDTO);
    }

    @Override
    public Mono<Void> deleteQuestionById(String questionId) {
        return questionRepository.deleteById(questionId).doOnSuccess(res -> {
            System.out.println("Question deleted with ID: " + questionId);
        }).doOnError(err -> {
            System.err.println("Error deleting question: " + err.getMessage());
        });
    }

    @Override
    public Mono<PaginatedResponse<QuestionResponseDTO>> searchQuestionsByTitleContaining(String title, int page, int size) {
        final int effectiveSize = Math.max(1, size);
        Pageable pageable = PageRequest.of(page, effectiveSize, Sort.by("createdAt").descending());
        Mono<List<QuestionResponseDTO>> pageDataMono = questionRepository
                .findByTitleContainingIgnoreCase(title, pageable)
                .map(QuestionMapper::toQuestionResponseDTO)
                .collectList();

        Mono<Long> totalCountMono = questionRepository.countByTitleContainingIgnoreCase(title);
        return Mono.zip(pageDataMono, totalCountMono)
                .map( tuple -> {
                    List<QuestionResponseDTO> questionList = tuple.getT1();
                    Long totalItems = tuple.getT2();

                    int totalPages = (int) Math.ceil((double) totalItems / effectiveSize);

                    String nextUrl = (page + 1 < totalPages)
                            ? String.format("/api/questions/search?title=%s&page=%d&size=%d", title, page + 1, effectiveSize)
                            : null;

                    PaginationMeta meta = PaginationMeta.builder()
                            .totalItems(totalItems)
                            .currentPage(page)
                            .pageSize(effectiveSize)
                            .totalPages(totalPages)
                            .nextPageUrl(nextUrl)
                            .build();

                    return PaginatedResponse.<QuestionResponseDTO>builder()
                            .data(questionList)
                            .pagination(meta)
                            .build();
                });
    }

    // TODO - FIX: This cursor logic will skip items if multiple questions have the exact same 'createdAt' timestamp.
    // To fix this 100%, we must use Keyset Pagination, which involves a composite cursor
    // (e.g., createdAt + _id) break ties.
    @Override
    public Mono<CursorPaginatedResponse<QuestionResponseDTO>> getPaginatedQuestions(String cursor, int size) {

        final int effectiveSize = Math.max(1, size) + 1; // Fetch (size + 1)
        final int requestedSize = Math.max(1, size);   // The size the client actually wants

        Pageable pageable = PageRequest.of(0, effectiveSize, Sort.by("createdAt").descending());

        Flux<Question> questionFlux;

        if (!CursorUtils.isValidCursor(cursor)) {
            // --- FIRST PAGE REQUEST (NO CURSOR) ---
            questionFlux = questionRepository.findAll(pageable);
        } else {
            LocalDateTime cursorTimeStamp = CursorUtils.parseCursor(cursor);
            // find items *OLDER* than the cursor (LessThan)
            questionFlux = questionRepository.findByCreatedAtLessThanOrderByCreatedAtDesc(cursorTimeStamp, pageable);
        }

        Mono<List<Question>> questionListMono = questionFlux.collectList();

        return questionListMono.map(questions -> {

            boolean hasMore = questions.size() > requestedSize;

            List<Question> dataList = hasMore ? questions.subList(0, requestedSize) : questions;

            List<QuestionResponseDTO> dtoList = dataList.stream()
                    .map(QuestionMapper::toQuestionResponseDTO)
                    .collect(Collectors.toList());

            String nextCursor = null;
            if (hasMore) {
                Question lastQuestion = dataList.get(dataList.size() - 1);
                nextCursor = CursorUtils.createCursor(lastQuestion.getCreatedAt());
            }

            CursorPaginationMeta meta = CursorPaginationMeta.builder()
                    .nextCursor(nextCursor)
                    .hasMore(hasMore)
                    .pageSize(requestedSize)
                    .build();

            return CursorPaginatedResponse.<QuestionResponseDTO>builder()
                    .data(dtoList)
                    .pagination(meta)
                    .build();
        });
    }

    @Override
    public Mono<CursorPaginatedResponse<QuestionResponseDTO>> pollNewQuestions(String cursor, int size) {
        final int effectiveSize = Math.max(1, size);
        Pageable pageable = PageRequest.of(0, effectiveSize, Sort.by("createdAt").descending());

        if (!CursorUtils.isValidCursor(cursor)) {
            return Mono.error(new IllegalArgumentException("A valid cursor is required for polling."));
        }

        LocalDateTime cursorTimeStamp = CursorUtils.parseCursor(cursor);
        Flux<Question> questionFlux = questionRepository
                .findByCreatedAtGreaterThanOrderByCreatedAtDesc(cursorTimeStamp, pageable);

        return questionFlux.collectList()
                .map(questions -> {

                    List<QuestionResponseDTO> dtoList = questions.stream()
                            .map(QuestionMapper::toQuestionResponseDTO)
                            .collect(Collectors.toList());

                    String newCursor = cursor;
                    if (!questions.isEmpty()) {
                        Question newestQuestion = questions.get(0);
                        newCursor = CursorUtils.createCursor(newestQuestion.getCreatedAt());
                    }

                    CursorPaginationMeta meta = CursorPaginationMeta.builder()
                            .nextCursor(newCursor)
                            .hasMore(false)
                            .pageSize(effectiveSize)
                            .build();

                    return CursorPaginatedResponse.<QuestionResponseDTO>builder()
                            .data(dtoList)
                            .pagination(meta)
                            .build();
                })
                .doOnError(err -> {
                    System.err.println("Error polling new questions: " + err.getMessage());
                });
    }
}
