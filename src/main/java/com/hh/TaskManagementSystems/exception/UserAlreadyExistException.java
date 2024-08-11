package com.hh.TaskManagementSystems.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String message) {
        super("Пользователь с таким " + message + " уже существует");
    }
}
