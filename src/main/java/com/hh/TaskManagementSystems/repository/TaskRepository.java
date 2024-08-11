package com.hh.TaskManagementSystems.repository;

import com.hh.TaskManagementSystems.model.Task;
import com.hh.TaskManagementSystems.model.TaskPriority;
import com.hh.TaskManagementSystems.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAuthor_Email(String email);

    List<Task> findByExecutor_Email(String email);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByPriority(TaskPriority priority, Pageable pageable);

    Page<Task> findByCommentsNotEmpty(Pageable pageable);

    Page<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority, Pageable pageable);


}
