package com.project.quora.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AnswerRequestDTO {
    @NotBlank
    @NotEmpty
    private String questionId;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 250, message = "Title must be between 1 and 250 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 10000, message = "Content must be between 1 and 10000 characters")
    private String content;
}
