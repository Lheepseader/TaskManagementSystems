package com.hh.TaskManagementSystems.repository;

import com.hh.TaskManagementSystems.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByTask_Id(Long id, Pageable pageable);

}
