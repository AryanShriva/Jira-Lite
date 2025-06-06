package com.shriva.jira_lite_backend_java.controller;

import com.shriva.jira_lite_backend_java.entity.Task;
import com.shriva.jira_lite_backend_java.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(
            @PathVariable Long projectId,
            @RequestBody Task task,
            @RequestParam(required = false) Long assignedToId) {
        Task createdTask = taskService.createTask(projectId, task, assignedToId);
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasksByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody Task task,
            @RequestParam(required = false) Long assignedToId) {
        Task updatedTask = taskService.updateTask(projectId, taskId, task, assignedToId);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        taskService.deleteTask(projectId, taskId);
        return ResponseEntity.noContent().build();
    }
}