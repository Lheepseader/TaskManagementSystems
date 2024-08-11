package com.hh.TaskManagementSystems.service;

import com.hh.TaskManagementSystems.converter.TaskConverter;
import com.hh.TaskManagementSystems.dto.CommentDto;
import com.hh.TaskManagementSystems.dto.TaskDto;
import com.hh.TaskManagementSystems.exception.NotEnoughRightsException;
import com.hh.TaskManagementSystems.exception.NotFoundException;
import com.hh.TaskManagementSystems.model.Task;
import com.hh.TaskManagementSystems.model.TaskPriority;
import com.hh.TaskManagementSystems.model.TaskStatus;
import com.hh.TaskManagementSystems.model.User;
import com.hh.TaskManagementSystems.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления задачами.
 *
 * <p>Этот сервис предоставляет методы для создания, обновления, удаления и получения задач,
 * а также для управления их статусом и исполнителями.</p>
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskConverter taskConverter;
    private final CommentService commentService;

    /**
     * Сохраняет новую задачу.
     *
     * <p>Создает задачу, устанавливая текущего пользователя как автора, и сохраняет её в базе данных.</p>
     *
     * @param taskDto DTO объекта задачи, который нужно сохранить
     * @return сохраненная задача в формате DTO
     */
    public TaskDto saveTask(TaskDto taskDto) {
        User user = userService.getCurrentUser();
        Task task = taskConverter.toEntity(taskDto);
        task.setAuthor(user);
        return taskConverter.toDto(taskRepository.save(task));
    }

    /**
     * Обновляет задачу по идентификатору.
     *
     * <p>Проверяет, является ли текущий пользователь автором задачи. Если да, обновляет задачу и сохраняет её в базе данных.</p>
     *
     * @param id             идентификатор задачи, которую нужно обновить
     * @param updatedTaskDto DTO объекта задачи с обновленными данными
     * @return обновленная задача в формате DTO
     * @throws NotEnoughRightsException  если текущий пользователь не является автором задачи
     * @throws NotFoundException если задача с указанным идентификатором не найдена
     */
    public TaskDto updateTask(Long id, TaskDto updatedTaskDto) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Id"));
        User user = userService.getCurrentUser();
        if (isAuthorOfTask(user, task)) {
            Task updatedTask = taskConverter.toEntity(updatedTaskDto);
            updatedTask.setAuthor(user);
            updatedTask.setId(id);
            updatedTask.setComments(task.getComments());
            return taskConverter.toDto(taskRepository.save(updatedTask));
        }
        throw new NotEnoughRightsException();
    }

    /**
     * Удаляет задачу по идентификатору.
     *
     * <p>Проверяет, является ли текущий пользователь автором задачи. Если да, удаляет задачу из базы данных.</p>
     *
     * @param id идентификатор задачи, которую нужно удалить
     * @throws NotEnoughRightsException  если текущий пользователь не является автором задачи
     * @throws NotFoundException если задача с указанным идентификатором не найдена
     */
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id"));
        User user = userService.getCurrentUser();
        if (isAuthorOfTask(user, task)) {
            taskRepository.deleteById(id);
        } else {
            throw new NotEnoughRightsException();
        }
    }

    /**
     * Обновляет статус задачи.
     *
     * <p>Проверяет, является ли текущий пользователь автором или исполнителем задачи. Если да, обновляет статус задачи.</p>
     *
     * @param id     идентификатор задачи, статус которой нужно обновить
     * @param status новый статус задачи
     * @return обновленная задача в формате DTO
     * @throws NotEnoughRightsException  если текущий пользователь не является автором или исполнителем задачи
     * @throws NotFoundException если задача с указанным идентификатором не найдена
     */
    public TaskDto updateStatus(Long id, TaskStatus status) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Id"));
        User user = userService.getCurrentUser();
        if (isAuthorOfTask(user, task) || isExecutorOfTask(user, task)) {
            task.setStatus(status);
            return taskConverter.toDto(taskRepository.save(task));
        }
        throw new NotEnoughRightsException();
    }

    /**
     * Обновляет исполнителя задачи.
     *
     * <p>Проверяет, является ли текущий пользователь автором задачи. Если да, обновляет исполнителя задачи.</p>
     *
     * @param id    идентификатор задачи, у которой нужно обновить исполнителя
     * @param email email нового исполнителя задачи
     * @return обновленная задача в формате DTO
     * @throws NotEnoughRightsException  если текущий пользователь не является автором задачи
     * @throws NotFoundException если задача с указанным идентификатором не найдена или пользователь не найден
     */
    public TaskDto updateExecutor(Long id, String email) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Id"));
        User user = userService.getCurrentUser();
        if (isAuthorOfTask(user, task)) {
            User executor = (User) userService.userDetailsService().loadUserByUsername(email);
            task.setExecutor(executor);
            return taskConverter.toDto(taskRepository.save(task));
        }
        throw new NotEnoughRightsException();
    }

    /**
     * Проверяет, является ли пользователь автором задачи.
     *
     * @param user текущий пользователь
     * @param task задача
     * @return {@code true}, если пользователь является автором задачи, иначе {@code false}
     */
    private boolean isAuthorOfTask(User user, Task task) {
        return user.getEmail().equals(task.getAuthor().getEmail());
    }

    /**
     * Проверяет, является ли пользователь исполнителем задачи.
     *
     * @param user текущий пользователь
     * @param task задача
     * @return {@code true}, если пользователь является исполнителем задачи, иначе {@code false}
     */
    private boolean isExecutorOfTask(User user, Task task) {
        return user.getEmail().equals(task.getExecutor().getEmail());
    }

    /**
     * Получает все задачи, созданные пользователем по его email.
     *
     * @param authorEmail email автора задач
     * @return список задач, созданных пользователем, в формате DTO
     */
    public List<TaskDto> getCreatedTasks(String authorEmail) {
        List<Task> tasks = taskRepository.findByAuthor_Email(authorEmail);
        return tasks.stream().map(taskConverter::toDto).toList();
    }

    /**
     * Получает все задачи, назначенные на исполнителя по его email.
     *
     * @param executorEmail email исполнителя задач
     * @return список задач, назначенных на исполнителя, в формате DTO
     */
    public List<TaskDto> getTasksToComplete(String executorEmail) {
        List<Task> tasks = taskRepository.findByExecutor_Email(executorEmail);
        return tasks.stream().map(taskConverter::toDto).toList();
    }

    /**
     * Получает все задачи с поддержкой постраничного вывода.
     *
     * @param pageable объект для настройки постраничного вывода
     * @return список всех задач в формате DTO
     */
    public List<TaskDto> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable).map(taskConverter::toDto).toList();
    }

    /**
     * Получает все задачи с указанным статусом и поддержкой постраничного вывода.
     *
     * @param status   статус задач
     * @param pageable объект для настройки постраничного вывода
     * @return список задач с указанным статусом в формате DTO
     */
    private List<TaskDto> getAllTasks(TaskStatus status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable).map(taskConverter::toDto).toList();
    }

    /**
     * Получает все задачи с указанным приоритетом и поддержкой постраничного вывода.
     *
     * @param priority приоритет задач
     * @param pageable объект для настройки постраничного вывода
     * @return список задач с указанным приоритетом в формате DTO
     */
    private List<TaskDto> getAllTasks(TaskPriority priority, Pageable pageable) {
        return taskRepository.findByPriority(priority, pageable).map(taskConverter::toDto).toList();
    }

    /**
     * Получает список объектов {@link TaskDto} на основе заданных фильтров и параметров пагинации.
     * <p>
     * Этот метод поддерживает фильтрацию задач по статусу и приоритету. Если указаны оба фильтра,
     * возвращаются задачи, соответствующие обоим критериям. Если указан только один из фильтров,
     * возвращаются задачи, соответствующие этому фильтру. Если фильтры не указаны, возвращаются все задачи
     * в соответствии с параметрами пагинации.
     *
     * @param status   статус, по которому фильтруются задачи. Если {@code null}, задачи не фильтруются по статусу.
     *                 Ожидаемые значения — это те, которые определены в {@link TaskStatus}.
     * @param priority приоритет, по которому фильтруются задачи. Если {@code null}, задачи не фильтруются по приоритету.
     *                 Ожидаемые значения — это те, которые определены в {@link TaskPriority}.
     * @param pageable параметры пагинации и сортировки.
     * @return список объектов {@link TaskDto}, соответствующих указанным фильтрам и параметрам пагинации.
     * Если фильтры не указаны, возвращаются все задачи.
     * @throws IllegalArgumentException если предоставленные значения статуса или приоритета не являются допустимыми
     *                                  {@link TaskStatus} или {@link TaskPriority} соответственно.
     */
    public List<TaskDto> getAllTasks(String status, String priority, Pageable pageable) {
        TaskStatus taskStatus = status != null ? TaskStatus.valueOf(status) : null;
        TaskPriority taskPriority = priority != null ? TaskPriority.valueOf(priority) : null;

        if (taskStatus != null && taskPriority != null) {
            return taskRepository.findByStatusAndPriority(taskStatus, taskPriority, pageable)
                    .map(taskConverter::toDto)
                    .toList();
        } else if (taskPriority != null) {
            return getAllTasks(taskPriority, pageable);
        } else if (taskStatus != null) {
            return getAllTasks(taskStatus, pageable);
        } else {
            return getAllTasks(pageable);
        }
    }

    /**
     * Получает задачу по идентификатору и комментарии к задаче с поддержкой постраничного вывода.
     *
     * @param id       идентификатор задачи
     * @param pageable объект для настройки постраничного вывода комментариев
     * @return задача с комментариями в формате DTO
     * @throws NotFoundException если задача с указанным идентификатором не найдена
     */
    public TaskDto getTaskById(Long id, Pageable pageable) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Id"));
        Page<CommentDto> commentDtoPage = commentService.getCommentsByTaskId(id, pageable);
        return taskConverter.toDto(task, commentDtoPage);
    }
}
