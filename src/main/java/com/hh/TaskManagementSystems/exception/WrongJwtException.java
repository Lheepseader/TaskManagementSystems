package com.hh.TaskManagementSystems.exception;

public class WrongJwtException extends RuntimeException{
    public WrongJwtException() {
        super("Неверный JWT токен");
    }
}
