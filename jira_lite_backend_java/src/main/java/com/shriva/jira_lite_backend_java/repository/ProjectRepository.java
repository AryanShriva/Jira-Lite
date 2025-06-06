package com.shriva.jira_lite_backend_java.repository;

import com.shriva.jira_lite_backend_java.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:priority IS NULL OR p.priority = :priority)")
    List<Project> findByStatusAndPriority(@Param("status") Project.ProjectStatus status,
                                          @Param("priority") Project.ProjectPriority priority);
}