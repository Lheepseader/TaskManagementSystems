package com.hh.TaskManagementSystems.dto;

import com.hh.TaskManagementSystems.model.TaskPriority;
import com.hh.TaskManagementSystems.model.TaskStatus;
import com.hh.TaskManagementSystems.validator.EnumValid;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.domain.Page;

@Builder
public record TaskDto(

        @Positive(message = "Id не может быть меньше 1")
        @Schema(description = "Id задачи", example = "1")
        Long id,
        @Schema(description = "Заголовок задачи", example = "Пример заголовка")
        @Size(max = 100)
        @NotBlank(message = "Заголовок не может быть пустым")
        String title,
        @Schema(description = "Описание задачи", example = "Пример описания")
        @Size(max = 4000)
        @NotBlank(message = "Описание не может быть пустым")
        String description,
        @Schema(description = "Статус задачи", example = "PENDING")
        @EnumValid(enumClass = TaskStatus.class, message = "Неправильный статус задачи")
        String status,
        @Schema(description = "Приоритет задачи", example = "HIGH")
        @EnumValid(enumClass = TaskPriority.class, message = "Неправильный приоритет задачи")
        String priority,
        @Schema(description = "Адрес электронной почты исполнителя", example = "example@example.com")
        @Email(message = "Email должен быть в формате example@example.com")
        @Size(max = 255, message = "Email должен содержать до 255 символов")
        String executorEmail,
        @Schema(description = "Адрес электронной почты автора, заполняется автоматически")
        String authorEmail,
        @Schema(description = "Комментарии к задаче")
        Page<CommentDto> comments
) {
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        TaskDto taskDto = (TaskDto) o;

        return new EqualsBuilder().append(title, taskDto.title)
                .append(description, taskDto.description)
                .append(status, taskDto.status)
                .append(priority, taskDto.priority)
                .append(executorEmail, taskDto.executorEmail)
                .append(authorEmail, taskDto.authorEmail)
                .append(comments, taskDto.comments)
                .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37).append(title)
                .append(description)
                .append(status)
                .append(priority)
                .append(executorEmail)
                .append(authorEmail)
                .append(comments)
                .toHashCode();
    }
}
