package com.hh.TaskManagementSystems.exception;

public class NotEnoughRightsException extends RuntimeException{
    public NotEnoughRightsException() {
        super("Недостаточно прав");
    }
}

