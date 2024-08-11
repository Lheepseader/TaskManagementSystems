package com.hh.TaskManagementSystems.controller;

import com.hh.TaskManagementSystems.dto.TaskDto;
import com.hh.TaskManagementSystems.model.TaskPriority;
import com.hh.TaskManagementSystems.model.TaskStatus;
import com.hh.TaskManagementSystems.service.CommentService;
import com.hh.TaskManagementSystems.service.TaskService;
import com.hh.TaskManagementSystems.validator.EnumValid;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Создать задачу")
    public ResponseEntity<TaskDto> saveTask(@Valid @RequestBody TaskDto taskDto) {
        return ResponseEntity.ok(taskService.saveTask(taskDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить задачу")
    public ResponseEntity<TaskDto> updateTask(@PathVariable @Positive(message = "Id не может быть меньше 1") Long id,
                                              @Valid @RequestBody TaskDto taskDto) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDto));
    }

    @PutMapping("/{id}/update-status")
    @Operation(summary = "Изменить статус задачи")
    public ResponseEntity<TaskDto> updateTaskStatus(
            @PathVariable @Positive(message = "Id не может быть меньше 1") Long id,
            @EnumValid(enumClass = TaskStatus.class, message = "Неправильный статус задачи")
            @NotBlank(message = "Статус не может быть пустым")
            @RequestBody TaskStatus taskStatus) {
        return ResponseEntity.ok(taskService.updateStatus(id, taskStatus));
    }

    @PutMapping("/{id}/update-executor")
    @Operation(summary = "Изменить исполнителя задачи")
    public ResponseEntity<TaskDto> updateTaskExecutor(
            @PathVariable @Positive(message = "Id не может быть меньше 1") Long id,
            @RequestBody
            @Email(message = "Email должен быть в формате example@example.com")
            @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
            @NotBlank(message = "Email не может быть пустым")
            String email) {
        return ResponseEntity.ok(taskService.updateExecutor(id, email));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить задачу")
    public ResponseEntity<Void> deleteTask(@PathVariable @Positive(message = "Id не может быть меньше 1") Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/created/{email}")
    @Operation(summary = "Получить задачи по автору")
    public ResponseEntity<List<TaskDto>> getCreatedTasks(
            @PathVariable String email) {
        return ResponseEntity.ok(taskService.getCreatedTasks(email));
    }

    @GetMapping("/to-complete/{email}")
    @Operation(summary = "Получить задачи по исполнителю")
    public ResponseEntity<List<TaskDto>> getTasksToComplete(
            @PathVariable String email) {
        return ResponseEntity.ok(taskService.getTasksToComplete(email));
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все задачи с пагинацией и фильтрацией")
    public ResponseEntity<List<TaskDto>> getAllTasks(
            @RequestParam(required = false)
            @EnumValid(enumClass = TaskStatus.class, message = "Неправильный статус задачи") String status,
            @RequestParam(required = false)
            @EnumValid(enumClass = TaskPriority.class, message = "Неправильный приоритет задачи") String priority,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "title") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(taskService.getAllTasks(status, priority, pageable));

    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить задачу по id с пагинацией и фильтрацией комментариев")
    public ResponseEntity<TaskDto> getTask(@PathVariable @Positive(message = "Id не может быть меньше 1") Long id,
                                           @RequestParam(defaultValue = "0") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(defaultValue = "dateCreation") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(taskService.getTaskById(id, pageable));
    }
}
