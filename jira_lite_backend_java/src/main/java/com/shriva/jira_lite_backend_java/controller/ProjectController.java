package com.shriva.jira_lite_backend_java.controller;

import com.shriva.jira_lite_backend_java.entity.Project;
import com.shriva.jira_lite_backend_java.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Project> createProject(
            @RequestBody Project project,
            @RequestParam Long assignedToId,
            @RequestParam Long assignedById) {
        try {
            Project createdProject = projectService.createProject(project, assignedToId, assignedById);
            return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER', 'MANAGER')")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER', 'MANAGER')")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        try {
            Project project = projectService.getProjectById(id);
            return ResponseEntity.ok(project);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long id,
            @RequestBody Project project,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(required = false) Long assignedById) {
        try {
            Project updatedProject = projectService.updateProject(id, project, assignedToId, assignedById);
            return ResponseEntity.ok(updatedProject);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER', 'MANAGER')")
    public ResponseEntity<List<Project>> filterProjects(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        Project.ProjectStatus projectStatus = null;
        Project.ProjectPriority projectPriority = null;

        if (status != null && !status.isEmpty()) {
            try {
                projectStatus = Project.ProjectStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        }

        if (priority != null && !priority.isEmpty()) {
            try {
                projectPriority = Project.ProjectPriority.valueOf(priority);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        }

        List<Project> projects = projectService.filterProjects(projectStatus, projectPriority);
        return ResponseEntity.ok(projects);
    }
}