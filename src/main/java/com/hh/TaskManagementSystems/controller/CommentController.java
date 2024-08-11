package com.hh.TaskManagementSystems.controller;

import com.hh.TaskManagementSystems.dto.CommentDto;
import com.hh.TaskManagementSystems.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Создать комментарий")
    public ResponseEntity<CommentDto> saveComment(
            @PathVariable @Positive(message = "Id не может быть меньше 1") Long taskId,
            @Valid @RequestBody CommentDto commentDto) {

        return ResponseEntity.ok(commentService.saveComment(taskId, commentDto));
    }

    @GetMapping("/all")
    @Operation(summary = "Получить комментарии по id задачи")
    public Page<CommentDto> getCommentsForTask(@PathVariable Long taskId,
                                               @RequestParam(defaultValue = "0") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return commentService.getCommentsByTaskId(taskId, pageable);
    }
}
