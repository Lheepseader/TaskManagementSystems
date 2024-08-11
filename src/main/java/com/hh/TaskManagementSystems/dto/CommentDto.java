package com.hh.TaskManagementSystems.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Builder
public record CommentDto(
        @Schema(description = "Тело комментария", example = "Сделай это вот так.")
        @NotBlank(message = "Комментарий не можем быть пустым")
        @Size(max = 255, message = "Тело комментария должен содержать до 255 символов")
        String body,
        @Schema(description = "Id задачи", example = "1")
        @Positive(message = "Id не может быть меньше 1")
        Long taskId,
        @Schema(description = "Адрес электронной почты автора", example = "example@example.com")
        @Email(message = "Email должен быть в формате example@example.com")
        @Size(max = 255, message = "Email должен содержать до 255 символов")
        String authorEmail,
        @Schema(description = "Дата создания", example = "dow mon dd hh:mm:ss zzz yyyy")
        String dateCreation
) {
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CommentDto that = (CommentDto) o;

        return new EqualsBuilder().append(body, that.body)
                .append(taskId, that.taskId)
                .append(authorEmail, that.authorEmail)
                .append(dateCreation, that.dateCreation)
                .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37).append(body)
                .append(taskId)
                .append(authorEmail)
                .append(dateCreation)
                .toHashCode();
    }
}

