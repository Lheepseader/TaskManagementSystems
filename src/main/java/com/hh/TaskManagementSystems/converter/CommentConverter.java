package com.hh.TaskManagementSystems.converter;

import com.hh.TaskManagementSystems.dto.CommentDto;
import com.hh.TaskManagementSystems.exception.NotFoundException;
import com.hh.TaskManagementSystems.model.Comment;
import com.hh.TaskManagementSystems.repository.TaskRepository;
import com.hh.TaskManagementSystems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Конвертер для преобразования между сущностями {@link Comment} и {@link CommentDto}.
 *
 * <p>Этот компонент предоставляет методы для преобразования данных комментариев между внутренним представлением
 * {@link Comment} и DTO объектами {@link CommentDto} для передачи по сети или отображения на клиенте.</p>
 */
@Component
@RequiredArgsConstructor
public class CommentConverter {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    /**
     * Преобразует сущность {@link Comment} в {@link CommentDto}.
     *
     * <p>Если комментарий или его связанные объекты равны {@code null}, возвращается {@code null}.</p>
     *
     * @param comment сущность комментария
     * @return {@link CommentDto} представляющий комментарий, или {@code null} если комментарий равен {@code null}
     */
    public CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentDto.builder()
                .body(comment.getBody())
                .taskId(comment.getTask().getId())
                .authorEmail(comment.getAuthor().getEmail())
                .dateCreation(comment.getDateCreation().toString())
                .build();
    }

    /**
     * Преобразует {@link CommentDto} в сущность {@link Comment}.
     *
     * <p>Если DTO равен {@code null}, возвращается {@code null}. Если указанный идентификатор задачи или email автора
     * не найден в базе данных, возникает {@link NotFoundException}.</p>
     *
     * @param commentDto DTO комментария
     * @return сущность {@link Comment}, представляющая комментарий, или {@code null} если DTO равен {@code null}
     * @throws NotFoundException если задача или пользователь не найдены по указанным идентификаторам
     */
    public Comment toEntity(CommentDto commentDto) {
        if (commentDto == null) {
            return null;
        }

        return Comment.builder()
                .body(commentDto.body())
                .task(taskRepository.findById(commentDto.taskId()).orElseThrow(() -> new NotFoundException("Id")))
                .author(userRepository.findByEmail(commentDto.authorEmail())
                        .orElse(null))
                .build();
    }

    /**
     * Преобразует {@link CommentDto} в сущность {@link Comment} с использованием указанного идентификатора задачи.
     *
     * <p>Если DTO равен {@code null}, возвращается {@code null}. Если указанный идентификатор задачи или email автора
     * не найден в базе данных, возникает {@link NotFoundException}.</p>
     *
     * @param commentDto DTO комментария
     * @param taskId     идентификатор задачи, к которой относится комментарий
     * @return сущность {@link Comment}, представляющая комментарий, или {@code null} если DTO равен {@code null}
     * @throws NotFoundException если задача или пользователь не найдены по указанным идентификаторам
     */
    public Comment toEntity(CommentDto commentDto, Long taskId) {
        if (commentDto == null) {
            return null;
        }

        return Comment.builder()
                .body(commentDto.body())
                .task(taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Id")))
                .author(userRepository.findByEmail(commentDto.authorEmail())
                        .orElse(null))
                .build();
    }
}
