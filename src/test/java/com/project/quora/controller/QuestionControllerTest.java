package com.project.quora.controller;

import com.project.quora.dto.*;
import com.project.quora.service.IQuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionControllerTest {

    @Mock
    private IQuestionService questionService;

    @InjectMocks
    private QuestionController questionController;

    private WebTestClient webTestClient;

    private QuestionResponseDTO questionResponseDTO;
    private QuestionRequestDTO questionRequestDTO;

    @BeforeEach
    void setUp() {
        // This is the reactive equivalent of MockMvcBuilders.standaloneSetup()
        webTestClient = WebTestClient.bindToController(questionController)
                // .setControllerAdvice(new GlobalExceptionHandler()) // for global exception handler
                .build();

        // Setup common test data
        questionResponseDTO = new QuestionResponseDTO();
        questionResponseDTO.setId("q1");
        questionResponseDTO.setTitle("Test Question Title");
        questionResponseDTO.setContent("Test Question Body");
        questionResponseDTO.setCreateAt(LocalDateTime.now());
        questionResponseDTO.setUpdatedAt(LocalDateTime.now());

        questionRequestDTO = new QuestionRequestDTO("New Title", "New Content");
    }

    @Test
    @DisplayName("GET /api/questions/{id} should return a question")
    void getQuestionById_shouldReturnQuestion() throws Exception {
        // Arrange
        when(questionService.getQuestionById("q1")).thenReturn(Mono.just(questionResponseDTO));

        // Act & Assert
        webTestClient.get().uri("/api/questions/q1")
                .exchange() // 'exchange()' is the reactive version of 'perform()'
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("q1")
                .jsonPath("$.title").isEqualTo("Test Question Title");

        // Verify
        verify(questionService, times(1)).getQuestionById("q1");
    }

    @Test
    @DisplayName("POST /api/questions should create a new question")
    void createQuestion_shouldCreateNewQuestion() throws Exception {
        // Arrange
        // The service returns a ResponseDTO *after* saving
        when(questionService.createQuestion(any(QuestionRequestDTO.class)))
                .thenReturn(Mono.just(questionResponseDTO));

        // Act & Assert
        webTestClient.post().uri("/api/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(questionRequestDTO) // Send the request DTO
                .exchange()
                .expectStatus().isOk() // 200 OK is the default for a successful Mono<T>
                .expectBody()
                .jsonPath("$.id").isEqualTo("q1") // Expect the response DTO
                .jsonPath("$.title").isEqualTo("Test Question Title");

        // Verify
        verify(questionService, times(1)).createQuestion(any(QuestionRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/questions/{id} should delete a question")
    void deleteQuestion_shouldDeleteQuestion() throws Exception {
        // Arrange
        when(questionService.deleteQuestionById("q1")).thenReturn(Mono.empty()); // Mono<Void>

        // Act & Assert
        webTestClient.delete().uri("/api/questions/q1")
                .exchange()
                .expectStatus().isOk() // 200 OK is default for successful Mono<Void>
                .expectBody().isEmpty(); // Nobody should be returned

        // Verify
        verify(questionService, times(1)).deleteQuestionById("q1");
    }

    @Test
    @DisplayName("GET /api/questions should return paginated questions")
    void getPaginatedQuestions_shouldReturnFirstPage() throws Exception {
        // Arrange
        CursorPaginationMeta meta = CursorPaginationMeta.builder()
                .nextCursor("next-cursor-string")
                .hasMore(true)
                .pageSize(2)
                .build();
        CursorPaginatedResponse<QuestionResponseDTO> paginatedResponse =
                CursorPaginatedResponse.<QuestionResponseDTO>builder()
                        .data(List.of(questionResponseDTO, questionResponseDTO))
                        .pagination(meta)
                        .build();

        // Test the default values (cursor=null, size=2)
        when(questionService.getPaginatedQuestions(eq(null), eq(2)))
                .thenReturn(Mono.just(paginatedResponse));

        // Act & Assert
        webTestClient.get().uri("/api/questions") // Uses default params
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(2)
                .jsonPath("$.data[0].id").isEqualTo("q1")
                .jsonPath("$.pagination.pageSize").isEqualTo(2)
                .jsonPath("$.pagination.hasMore").isEqualTo(true)
                .jsonPath("$.pagination.nextCursor").isEqualTo("next-cursor-string");

        // Verify
        verify(questionService, times(1)).getPaginatedQuestions(eq(null), eq(2));
    }

    @Test
    @DisplayName("GET /api/questions/search should return search results")
    void searchQuestionsByTitleContaining_shouldReturnResults() throws Exception {
        // Arrange
        PaginationMeta meta = PaginationMeta.builder()
                .totalItems(1L).currentPage(0).pageSize(10).totalPages(1).nextPageUrl(null)
                .build();
        PaginatedResponse<QuestionResponseDTO> paginatedResponse =
                PaginatedResponse.<QuestionResponseDTO>builder()
                        .data(List.of(questionResponseDTO))
                        .pagination(meta)
                        .build();

        when(questionService.searchQuestionsByTitleContaining("test", 0, 10))
                .thenReturn(Mono.just(paginatedResponse));

        // Act & Assert
        webTestClient.get().uri("/api/questions/search?title=test&page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].title").isEqualTo("Test Question Title")
                .jsonPath("$.pagination.totalItems").isEqualTo(1);

        // Verify
        verify(questionService, times(1)).searchQuestionsByTitleContaining("test", 0, 10);
    }

    @Test
    @DisplayName("GET /api/questions/poll should return new questions")
    void pollNewQuestions_shouldReturnNewQuestions() throws Exception {
        // Arrange
        String cursor = "some-cursor-string";
        int size = 5;

        CursorPaginationMeta meta = CursorPaginationMeta.builder()
                .nextCursor("new-cursor-string")
                .hasMore(false)
                .pageSize(size)
                .build();
        CursorPaginatedResponse<QuestionResponseDTO> pollResponse =
                CursorPaginatedResponse.<QuestionResponseDTO>builder()
                        .data(List.of(questionResponseDTO)) // One new question
                        .pagination(meta)
                        .build();

        when(questionService.pollNewQuestions(cursor, size))
                .thenReturn(Mono.just(pollResponse));

        // Act & Assert
        webTestClient.get().uri("/api/questions/poll?cursor={c}&size={s}", cursor, size)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.length()").isEqualTo(1)
                .jsonPath("$.pagination.nextCursor").isEqualTo("new-cursor-string");

        // Verify
        verify(questionService, times(1)).pollNewQuestions(cursor, size);
    }
}