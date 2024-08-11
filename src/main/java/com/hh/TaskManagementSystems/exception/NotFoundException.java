package com.hh.TaskManagementSystems.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message + " не найден");
    }
}
