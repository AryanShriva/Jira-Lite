package com.shriva.jira_lite_backend_java.controller;

import com.shriva.jira_lite_backend_java.entity.Project;
import com.shriva.jira_lite_backend_java.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<Project> createProject(
            @RequestBody Project project,
            @RequestParam Long assignedToId,
            @RequestParam Long assignedById) {
        Project createdProject = projectService.createProject(project, assignedToId, assignedById);
        return ResponseEntity.ok(createdProject);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long id,
            @RequestBody Project project,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(required = false) Long assignedById) {
        Project updatedProject = projectService.updateProject(id, project, assignedToId, assignedById);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}