package com.shriva.jira_lite_backend_java.repository;

import com.shriva.jira_lite_backend_java.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}