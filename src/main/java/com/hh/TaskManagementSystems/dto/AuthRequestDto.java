package com.hh.TaskManagementSystems.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public record AuthRequestDto(
        @Schema(description = "Адрес электронной почты", example = "example@example.com")
        @Email(message = "Email должен быть в формате example@example.com")
        @Size(max = 255, message = "Email должен содержать до 255 символов")
        @NotBlank(message = "Email не может быть пустым")
        String email,
        @Schema(description = "Пароль", example = "my_password")
        @Size(max = 255, message = "Пароль должен содержать до 255 символов")
        @NotBlank(message = "Пароль не может быть пустым")
        String password
) {
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        AuthRequestDto that = (AuthRequestDto) o;

        return new EqualsBuilder().append(email, that.email)
                .append(password, that.password)
                .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37).append(email).append(password).toHashCode();
    }
}
