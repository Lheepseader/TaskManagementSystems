package com.hh.TaskManagementSystems.service;

import com.hh.TaskManagementSystems.converter.CommentConverter;
import com.hh.TaskManagementSystems.dto.CommentDto;
import com.hh.TaskManagementSystems.model.Comment;
import com.hh.TaskManagementSystems.model.User;
import com.hh.TaskManagementSystems.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с комментариями.
 *
 * <p>Этот сервис предоставляет методы для получения комментариев по задаче и сохранения новых комментариев.</p>
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;
    private final UserService userService;

    /**
     * Получает комментарии по идентификатору задачи.
     *
     * <p>Возвращает страницу комментариев, связанных с указанной задачей, с возможностью постраничного отображения.</p>
     *
     * @param id       идентификатор задачи
     * @param pageable параметры постраничного отображения
     * @return страница комментариев для указанной задачи
     */
    public Page<CommentDto> getCommentsByTaskId(Long id, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByTask_Id(id, pageable);
        return comments.map(commentConverter::toDto);
    }

    /**
     * Сохраняет новый комментарий.
     *
     * <p>Сохраняет комментарий, связанный с указанной задачей, и присваивает авторство текущему пользователю.</p>
     *
     * @param taskId     идентификатор задачи, к которой относится комментарий
     * @param commentDto данные комментария
     * @return сохраненный комментарий в виде DTO
     */
    public CommentDto saveComment(Long taskId, CommentDto commentDto) {
        Comment comment = commentConverter.toEntity(commentDto, taskId);
        User user = userService.getCurrentUser();
        comment.setAuthor(user);
        comment = commentRepository.save(comment);
        return commentConverter.toDto(comment);
    }
}
