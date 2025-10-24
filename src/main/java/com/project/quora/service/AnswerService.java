package com.project.quora.service;

import com.project.quora.dto.AnswerRequestDTO;
import com.project.quora.dto.AnswerResponseDTO;
import com.project.quora.dto.PaginatedResponse;
import com.project.quora.dto.PaginationMeta;
import com.project.quora.enums.TargetType;
import com.project.quora.event.AnswerCreatedEvent;
import com.project.quora.event.ViewCountEvent;
import com.project.quora.mapper.AnswerMapper;
import com.project.quora.model.Answer;
import com.project.quora.producer.KafkaEventProducer;
import com.project.quora.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService implements IAnswerService {

    private final AnswerRepository answerRepository;

    private final KafkaEventProducer kafkaEventProducer;

    @Override
    public Mono<AnswerResponseDTO> createAnswer(AnswerRequestDTO answerRequestDTO) {
        Answer answer = AnswerMapper.toAnswer(answerRequestDTO);
        return answerRepository.save(answer)
                .map(AnswerMapper::toAnswerResponseDTO)
                .doOnError(err -> System.out.println("Error creating answer: " + err.getMessage()))
                .doOnSuccess(res -> {
                    kafkaEventProducer.publishAnswerCreatedEvent(
                            AnswerCreatedEvent.builder()
                                    .answerId(answer.getId())
                                    .questionId(answer.getQuestionId())
                                    .answerAuthorId("user") // TODO: replace with actual user ID
                                    .questionAuthorId("user") // TODO: replace with actual user ID
                                    .answerText(answer.getContent())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
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
    public Mono<PaginatedResponse<AnswerResponseDTO>> getAllAnswers(int page, int size) {
        final int effectiveSize = Math.max(1, size);

        Pageable pageable = PageRequest.of(page, effectiveSize, Sort.by("createdAt").descending());

        Mono<List<AnswerResponseDTO>> pageDataMono = answerRepository.findAll(pageable)
                .map(AnswerMapper::toAnswerResponseDTO)
                .collectList();
        Mono<Long> totalCountMono = answerRepository.count();

        return Mono.zip(pageDataMono, totalCountMono)
                .map(tuple ->{
                    List<AnswerResponseDTO> answerList = tuple.getT1();
                    long totalItems = tuple.getT2();

                    int totalPages = (int) Math.ceil((double) totalItems / effectiveSize);

                    String nextUrl = (page + 1 < totalPages)
                            ? String.format("/api/answers?page=%d&size=%d", page + 1, effectiveSize)
                            : null;

                    PaginationMeta meta = PaginationMeta.builder()
                            .totalItems(totalItems)
                            .currentPage(page)
                            .pageSize(effectiveSize)
                            .totalPages(totalPages)
                            .nextPageUrl(nextUrl)
                            .build();

                    return PaginatedResponse.<AnswerResponseDTO>builder()
                            .data(answerList)
                            .pagination(meta)
                            .build();
                });
    }

    @Override
    public Mono<PaginatedResponse<AnswerResponseDTO>> getAnswersByQuestionId(String questionId, int page, int size) {
        final int effectiveSize = Math.max(1, size);
        Pageable pageable = PageRequest.of(page, effectiveSize, Sort.by("createdAt").descending());
        Mono<List<AnswerResponseDTO>> pageDataMono = answerRepository.findByQuestionId(questionId, pageable)
                .map(AnswerMapper::toAnswerResponseDTO)
                .doOnNext(answerDTO -> {
                    kafkaEventProducer.publishViewCountEvent(
                            ViewCountEvent.builder()
                                    .targetId(answerDTO.getId())
                                    .targetType(TargetType.ANSWER)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .collectList();
        Mono<Long> totalCountMono = answerRepository.countByQuestionId(questionId);

        return Mono.zip(pageDataMono, totalCountMono)
                .map(tuple -> {
                    List<AnswerResponseDTO> answerList = tuple.getT1();
                    long totalItems = tuple.getT2();

                    int totalPages = (int) Math.ceil((double) totalItems / effectiveSize);

                    String nextUrl = (page + 1 < totalPages)
                            ? String.format("/api/answers/question/%s?page=%d&size=%d", questionId, page + 1, effectiveSize)
                            : null;

                    PaginationMeta meta = PaginationMeta.builder()
                            .totalItems(totalItems)
                            .currentPage(page)
                            .pageSize(effectiveSize)
                            .totalPages(totalPages)
                            .nextPageUrl(nextUrl)
                            .build();

                    return PaginatedResponse.<AnswerResponseDTO>builder()
                            .data(answerList)
                            .pagination(meta)
                            .build();
                });
    }

    @Override
    public Mono<AnswerResponseDTO> getAnswerById(String answerId) {
        return answerRepository.findById(answerId)
                .map(AnswerMapper::toAnswerResponseDTO)
                .doOnError(err -> System.out.println("Error fetching answer: " + err.getMessage()))
                .doOnSuccess( res -> {
                    kafkaEventProducer.publishViewCountEvent(
                            ViewCountEvent.builder()
                                    .targetId(answerId)
                                    .targetType(TargetType.ANSWER)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }
}

