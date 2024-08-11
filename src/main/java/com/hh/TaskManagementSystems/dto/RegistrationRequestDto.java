package com.hh.TaskManagementSystems.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public record RegistrationRequestDto(
        @Schema(description = "Адрес электронной почты", example = "example@example.com")
        @Email(message = "Email должен быть в формате example@example.com")
        @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
        @NotBlank(message = "Email не может быть пустым")
        String email,
        @Schema(description = "Пароль", example = "my_password")
        @Size(min = 5, max = 255, message = "Пароль должен содержать от 5 до 255 символов")
        @NotBlank(message = "Пароль не может быть пустым")
        String password
) {
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        RegistrationRequestDto that = (RegistrationRequestDto) o;

        return new EqualsBuilder().append(email, that.email)
                .append(password, that.password)
                .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37).append(email).append(password).toHashCode();
    }
}
