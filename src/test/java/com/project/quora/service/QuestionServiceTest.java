package com.project.quora.service;

import com.project.quora.dto.CursorPaginatedResponse;
import com.project.quora.dto.PaginatedResponse;
import com.project.quora.dto.QuestionRequestDTO;
import com.project.quora.dto.QuestionResponseDTO;
import com.project.quora.enums.TargetType;
import com.project.quora.event.ViewCountEvent;
import com.project.quora.model.Question;
import com.project.quora.producer.KafkaEventProducer;
import com.project.quora.repository.QuestionRepository;
import com.project.quora.utils.CursorUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @InjectMocks
    private QuestionService questionService;

    private Question question1;
    private Question question2;
    private QuestionRequestDTO questionRequestDTO;
    private Question savedQuestion;

    @BeforeEach
    void setUp() {
        questionRequestDTO = new QuestionRequestDTO("Test Title", "Test Content");

        // This is what we'll pretend the DB returns *after* saving
        savedQuestion = new Question();
        savedQuestion.setId("new-q-id");
        savedQuestion.setTitle(questionRequestDTO.getTitle());
        savedQuestion.setContent(questionRequestDTO.getContent());
        savedQuestion.setCreatedAt(LocalDateTime.now());
        savedQuestion.setUpdatedAt(LocalDateTime.now());

        // For list operations
        question1 = new Question();
        question1.setId("q1");
        question1.setTitle("Title 1");
        question1.setContent("Body 1");
        question1.setCreatedAt(LocalDateTime.now().minusHours(1));
        question1.setUpdatedAt(LocalDateTime.now().minusHours(1));

        question2 = new Question();
        question2.setId("q2");
        question2.setTitle("Title 2");
        question2.setContent("Body 2");
        question2.setCreatedAt(LocalDateTime.now().minusDays(1));
        question2.setUpdatedAt(LocalDateTime.now().minusDays(1));
    }

    @Test
    @DisplayName("should create a new question successfully")
    void createQuestion_shouldCreateNewQuestion() {
        // Arrange
        // any(Question.class) is used because timestamps are set inside the service
        when(questionRepository.save(any(Question.class))).thenReturn(Mono.just(savedQuestion));

        // Act
        Mono<QuestionResponseDTO> resultMono = questionService.createQuestion(questionRequestDTO);

        // Assert
        StepVerifier.create(resultMono)
                .expectNextMatches(response -> {
                    assertEquals("new-q-id", response.getId());
                    assertEquals("Test Title", response.getTitle());
                    // We can also check the body/content
                    // assertEquals("Test Content", response.getBody()); // Assuming QuestionResponseDTO has body
                    return true;
                })
                .verifyComplete();

        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    @DisplayName("should handle error during question creation")
    void createQuestion_shouldHandleError() {
        // Arrange
        when(questionRepository.save(any(Question.class))).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<QuestionResponseDTO> resultMono = questionService.createQuestion(questionRequestDTO);

        // Assert
        StepVerifier.create(resultMono)
                .expectError(RuntimeException.class)
                .verify();

        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    @DisplayName("should return question by ID and publish view event")
    void getQuestionById_shouldReturnQuestionAndPublishEvent() {
        // Arrange
        when(questionRepository.findById("q1")).thenReturn(Mono.just(question1));
        // Mock the event producer since it returns void
        doNothing().when(kafkaEventProducer).publishViewCountEvent(any(ViewCountEvent.class));

        // Act
        Mono<QuestionResponseDTO> resultMono = questionService.getQuestionById("q1");

        // Assert
        StepVerifier.create(resultMono)
                .expectNextMatches(response -> {
                    assertEquals("q1", response.getId());
                    assertEquals("Title 1", response.getTitle());
                    return true;
                })
                .verifyComplete();

        verify(questionRepository, times(1)).findById("q1");
        // Verify that the side effect (publishing event) happened
        verify(kafkaEventProducer, times(1)).publishViewCountEvent(argThat(event ->
                event.getTargetId().equals("q1") && event.getTargetType().equals(TargetType.QUESTION)
        ));
    }

    @Test
    @DisplayName("should return all questions successfully")
    void getAllQuestions_shouldReturnAllQuestions() {
        // Arrange
        when(questionRepository.findAll()).thenReturn(Flux.just(question1, question2));

        // Act
        Flux<QuestionResponseDTO> resultFlux = questionService.getAllQuestions();

        // Assert
        StepVerifier.create(resultFlux)
                .expectNextCount(2)
                .verifyComplete();

        verify(questionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("should return empty flux when no questions exist")
    void getAllQuestions_shouldReturnEmptyFluxWhenNoQuestionsExist() {
        // Arrange
        when(questionRepository.findAll()).thenReturn(Flux.empty());

        // Act
        Flux<QuestionResponseDTO> resultFlux = questionService.getAllQuestions();

        // Assert
        StepVerifier.create(resultFlux)
                .expectNextCount(0)
                .verifyComplete();

        verify(questionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("should delete a question by ID successfully")
    void deleteQuestionById_shouldDeleteQuestion() {
        // Arrange
        when(questionRepository.deleteById("q1")).thenReturn(Mono.empty()); // Mono<Void> completes on success

        // Act
        Mono<Void> resultMono = questionService.deleteQuestionById("q1");

        // Assert
        StepVerifier.create(resultMono)
                .verifyComplete();

        verify(questionRepository, times(1)).deleteById("q1");
    }

    @Test
    @DisplayName("should search questions by title with pagination")
    void searchQuestionsByTitleContaining_shouldReturnPaginatedResponse() {
        // Arrange
        String title = "Title";
        int page = 0;
        int size = 1;

        List<Question> foundQuestions = List.of(question1); // Page 1 content
        Long totalCount = 2L; // Total items matching query

        when(questionRepository.findByTitleContainingIgnoreCase(eq(title), any(Pageable.class)))
                .thenReturn(Flux.fromIterable(foundQuestions));
        when(questionRepository.countByTitleContainingIgnoreCase(title))
                .thenReturn(Mono.just(totalCount));

        // Act
        Mono<PaginatedResponse<QuestionResponseDTO>> resultMono =
                questionService.searchQuestionsByTitleContaining(title, page, size);

        // Assert
        StepVerifier.create(resultMono)
                .expectNextMatches(response -> {
                    assertEquals(1, response.getData().size());
                    assertEquals("q1", response.getData().get(0).getId());
                    assertEquals(totalCount, response.getPagination().getTotalItems());
                    assertEquals(page, response.getPagination().getCurrentPage());
                    assertEquals(size, response.getPagination().getPageSize());
                    assertEquals(2, response.getPagination().getTotalPages()); // 2 total / 1 per page = 2 pages
                    assertNotNull(response.getPagination().getNextPageUrl());
                    return true;
                })
                .verifyComplete();

        verify(questionRepository, times(1)).findByTitleContainingIgnoreCase(eq(title), any(Pageable.class));
        verify(questionRepository, times(1)).countByTitleContainingIgnoreCase(title);
    }

    @Test
    @DisplayName("should get paginated questions for first page (no cursor)")
    void getPaginatedQuestions_shouldReturnFirstPage() {
        // Arrange
        int size = 1;
        // Service fetches size + 1
        List<Question> questionsFromDb = List.of(question1, question2);

        when(questionRepository.findAll(any(Pageable.class)))
                .thenReturn(Flux.fromIterable(questionsFromDb));

        // Act
        Mono<CursorPaginatedResponse<QuestionResponseDTO>> resultMono =
                questionService.getPaginatedQuestions(null, size);

        // Assert
        StepVerifier.create(resultMono)
                .expectNextMatches(response -> {
                    assertEquals(size, response.getData().size()); // Returns requested size
                    assertEquals("q1", response.getData().get(0).getId()); // Returns first item
                    assertTrue(response.getPagination().isHasMore()); // Has more (db returned 2, asked for 1)
                    assertNotNull(response.getPagination().getNextCursor());
                    // Cursor should be based on the *last* item in the returned list (question1)
                    assertEquals(CursorUtils.createCursor(question1.getCreatedAt()), response.getPagination().getNextCursor());
                    return true;
                })
                .verifyComplete();

        verify(questionRepository, times(1)).findAll(any(Pageable.class));
        verify(questionRepository, never()).findByCreatedAtLessThanOrderByCreatedAtDesc(any(), any());
    }

    @Test
    @DisplayName("should get paginated questions for next page (with cursor)")
    void getPaginatedQuestions_shouldReturnNextPageWithCursor() {
        // Arrange
        int size = 1;
        String cursor = CursorUtils.createCursor(question1.getCreatedAt()); // Cursor from question1

        // Service fetches size + 1. We only return 1 item (q2), so hasMore should be false.
        List<Question> questionsFromDb = List.of(question2);

        when(questionRepository.findByCreatedAtLessThanOrderByCreatedAtDesc(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Flux.fromIterable(questionsFromDb));

        // Act
        Mono<CursorPaginatedResponse<QuestionResponseDTO>> resultMono =
                questionService.getPaginatedQuestions(cursor, size);

        // Assert
        StepVerifier.create(resultMono)
                .expectNextMatches(response -> {
                    assertEquals(size, response.getData().size());
                    assertEquals("q2", response.getData().get(0).getId());
                    assertFalse(response.getPagination().isHasMore()); // DB returned 1, which is not > size
                    assertNull(response.getPagination().getNextCursor());
                    return true;
                })
                .verifyComplete();

        verify(questionRepository, never()).findAll(any(Pageable.class));
        verify(questionRepository, times(1)).findByCreatedAtLessThanOrderByCreatedAtDesc(any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    @DisplayName("should poll for new questions successfully")
    void pollNewQuestions_shouldReturnNewerQuestions() {
        // Arrange
        int size = 5;
        // Cursor from the oldest question
        String cursor = CursorUtils.createCursor(question2.getCreatedAt());

        // DB returns the newer question
        List<Question> newQuestions = List.of(question1);

        when(questionRepository.findByCreatedAtGreaterThanOrderByCreatedAtDesc(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Flux.fromIterable(newQuestions));

        // Act
        Mono<CursorPaginatedResponse<QuestionResponseDTO>> resultMono =
                questionService.pollNewQuestions(cursor, size);

        // Assert
        StepVerifier.create(resultMono)
                .expectNextMatches(response -> {
                    assertEquals(1, response.getData().size());
                    assertEquals("q1", response.getData().get(0).getId());
                    // New cursor should be from the *newest* item returned (question1)
                    assertEquals(CursorUtils.createCursor(question1.getCreatedAt()), response.getPagination().getNextCursor());
                    assertFalse(response.getPagination().isHasMore()); // Poll logic doesn't use hasMore
                    return true;
                })
                .verifyComplete();

        verify(questionRepository, times(1)).findByCreatedAtGreaterThanOrderByCreatedAtDesc(any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    @DisplayName("should return error when polling with invalid cursor")
    void pollNewQuestions_shouldReturnErrorForInvalidCursor() {
        // Arrange (No mocks needed, should fail fast)

        // Act
        Mono<CursorPaginatedResponse<QuestionResponseDTO>> resultMono =
                questionService.pollNewQuestions(null, 5);

        // Assert
        StepVerifier.create(resultMono)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(questionRepository, never()).findByCreatedAtGreaterThanOrderByCreatedAtDesc(any(), any());
    }
}
