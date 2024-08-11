package com.hh.TaskManagementSystems.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public record AuthResponseDto(@Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String jwt) {
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        AuthResponseDto that = (AuthResponseDto) o;

        return new EqualsBuilder().append(jwt, that.jwt).isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37).append(jwt).toHashCode();
    }
}
