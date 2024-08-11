package com.hh.TaskManagementSystems.converter;

import com.hh.TaskManagementSystems.dto.CommentDto;
import com.hh.TaskManagementSystems.dto.TaskDto;
import com.hh.TaskManagementSystems.model.Task;
import com.hh.TaskManagementSystems.model.TaskPriority;
import com.hh.TaskManagementSystems.model.TaskStatus;
import com.hh.TaskManagementSystems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Конвертер для преобразования между сущностями {@link Task} и {@link TaskDto}.
 *
 * <p>Этот компонент предоставляет методы для преобразования данных задачи и комментариев между внутренним представлением
 * {@link Task} и DTO объектами {@link TaskDto} для передачи по сети или отображения на клиенте.</p>
 */
@Component
@RequiredArgsConstructor
public class TaskConverter {

    private final UserRepository userRepository;

    /**
     * Преобразует сущность {@link Task} в {@link TaskDto}.
     *
     * <p>Если задача или ее связанные объекты равны {@code null}, возвращается {@code null}.</p>
     *
     * @param task сущность задачи
     * @return {@link TaskDto} представляющий задачу, или {@code null} если задача равна {@code null}
     */
    public TaskDto toDto(Task task) {
        if (task == null) {
            return null;
        }

        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus() != null ? task.getStatus().name() : null)
                .priority(task.getPriority() != null ? task.getPriority().name() : null)
                .executorEmail(task.getExecutor() != null ? task.getExecutor().getEmail() : null)
                .authorEmail(task.getAuthor().getEmail())
                .build();


    }

    /**
     * Преобразует сущность {@link Task} в {@link TaskDto} с комментариями.
     *
     * <p>Если задача или ее связанные объекты равны {@code null}, возвращается {@code null}.</p>
     *
     * @param task           сущность задачи
     * @param commentDtoPage страница с комментариями в виде {@link CommentDto}
     * @return {@link TaskDto} представляющий задачу и ее комментарии, или {@code null} если задача равна {@code null}
     */
    public TaskDto toDto(Task task, Page<CommentDto> commentDtoPage) {
        if (task == null) {
            return null;
        }

        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus() != null ? task.getStatus().name() : null)
                .priority(task.getPriority() != null ? task.getPriority().name() : null)
                .executorEmail(task.getExecutor() != null ? task.getExecutor().getEmail() : null)
                .authorEmail(task.getAuthor().getEmail())
                .comments(commentDtoPage)
                .build();


    }

    /**
     * Преобразует {@link TaskDto} в сущность {@link Task}.
     *
     * <p>Если DTO равен {@code null}, возвращается {@code null}. Если указанный email не найден в базе данных,
     * соответствующее поле будет установлено в {@code null}.</p>
     *
     * @param taskDto DTO задачи
     * @return сущность {@link Task}, представляющая задачу, или {@code null} если DTO равен {@code null}
     */

    public Task toEntity(TaskDto taskDto) {
        if (taskDto == null) {
            return null;
        }

        return Task.builder()
                .id(taskDto.id())
                .title(taskDto.title())
                .description(taskDto.description())
                .status(taskDto.status() != null ? TaskStatus.valueOf(taskDto.status()) : null)
                .priority(taskDto.priority() != null ? TaskPriority.valueOf(taskDto.priority()) : null)
                .executor(userRepository.findByEmail(taskDto.executorEmail())
                        .orElse(null))
                .author(userRepository.findByEmail(taskDto.authorEmail())
                        .orElse(null))
                .build();
    }
}
