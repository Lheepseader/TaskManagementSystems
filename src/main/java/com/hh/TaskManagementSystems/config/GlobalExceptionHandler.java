package com.hh.TaskManagementSystems.config;

import com.hh.TaskManagementSystems.exception.NotFoundException;
import com.hh.TaskManagementSystems.exception.UserAlreadyExistException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Глобальный обработчик исключений для REST API.
 *
 * <p>Этот класс обрабатывает исключения, возникающие в контроллерах, и формирует
 * соответствующие ответы с подробностями ошибок для клиента.</p>
 *
 * <p>Методы включают обработку общих исключений, таких как {@link NotFoundException},
 * а также специфических исключений, связанных с ошибками валидации аргументов.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    /**
     * Обрабатывает исключения типа {@link NotFoundException}.
     *
     * <p>Возвращает подробности проблемы с HTTP статусом 404 (Not Found) и сообщением об ошибке.</p>
     *
     * @param e исключение типа {@link RuntimeException}, обычно {@link NotFoundException}
     * @return объект {@link ProblemDetail} с деталями ошибки и статусом 404
     */

    @ExceptionHandler({NotFoundException.class, NotFoundException.class, UserAlreadyExistException.class})
    public ProblemDetail handleControllerException(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * Обрабатывает исключения, возникающие при недействительных аргументах методов.
     *
     * <p>Этот метод вызывается, когда аргументы метода контроллера не прошли валидацию.
     * Возвращает подробности проблемы с HTTP статусом 400 (Bad Request) и списком ошибок валидации.</p>
     *
     * @param ex      исключение {@link MethodArgumentNotValidException}, содержащее ошибки валидации
     * @param headers заголовки HTTP
     * @param status  статус ответа HTTP
     * @param request объект {@link WebRequest}, связанный с запросом
     * @return объект {@link ResponseEntity}, содержащий детали проблемы и статус 400
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        for (FieldError fe : ex.getFieldErrors()) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(fe.getField());
            stringBuilder.append(" - ");
            stringBuilder.append(fe.getDefaultMessage());
            stringBuilder.append(".");
        }
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                stringBuilder.toString());
        problemDetail.setTitle("Validation Error");


        return ResponseEntity.badRequest().body(problemDetail);
    }

    /**
     * Обрабатывает исключение {@link HandlerMethodValidationException}, которое возникает при валидации параметров метода
     * в контроллере Spring MVC. Этот метод переопределяет стандартное поведение обработки исключений в Spring и возвращает
     * кастомизированный ответ с подробной информацией об ошибках валидации.
     *
     * @param ex      исключение, содержащее информацию об ошибках валидации, возникших в процессе вызова метода, не должно быть {@code null}.
     * @param headers HTTP-заголовки, которые могут быть использованы для формирования ответа, не должны быть {@code null}.
     * @param status  HTTP-статус, который будет использован в ответе, не должен быть {@code null}.
     * @param request веб-запрос, который привел к возникновению исключения, не должен быть {@code null}.
     * @return {@link ResponseEntity}, содержащий детали ошибок валидации и HTTP-статус {@code 400 Bad Request}.
     */
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            @NonNull HandlerMethodValidationException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        for (MessageSourceResolvable fe : ex.getAllErrors()) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append(" ");
            }
            String fieldName = "parameter";
            if (fe instanceof FieldError fieldError) {
                fieldName = fieldError.getField();
            }
            stringBuilder.append(fieldName);
            stringBuilder.append(" - ");
            stringBuilder.append(fe.getDefaultMessage());
            stringBuilder.append(".");
        }
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                stringBuilder.toString());
        return ResponseEntity.badRequest().body(problemDetail);
    }
}
