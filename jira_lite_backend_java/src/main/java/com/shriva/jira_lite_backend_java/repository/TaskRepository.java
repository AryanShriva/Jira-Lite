package com.shriva.jira_lite_backend_java.repository;

import com.shriva.jira_lite_backend_java.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority)")
    List<Task> findByStatusAndPriority(@Param("status") Task.TaskStatus status,
                                       @Param("priority") Task.TaskPriority priority);
}