package com.shriva.jira_lite_backend_java.controller;

import com.shriva.jira_lite_backend_java.entity.Task;
import com.shriva.jira_lite_backend_java.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTask(
            @RequestBody Task task,
            @RequestParam Long assignedToId,
            @RequestParam Long assignedById) {
        try {
            Task createdTask = taskService.createTask(task, assignedToId, assignedById);
            return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create task: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER', 'MANAGER')")
    public ResponseEntity<?> getAllTasks() {
        try {
            List<Task> tasks = taskService.getAllTasks();
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve tasks: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER', 'MANAGER')")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            Task task = taskService.getTaskById(id);
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve task: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTask(
            @PathVariable Long id,
            @RequestBody Task task,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(required = false) Long assignedById) {
        try {
            Task updatedTask = taskService.updateTask(id, task, assignedToId, assignedById);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update task: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete task: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER', 'MANAGER')")
    public ResponseEntity<?> filterTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        Task.TaskStatus taskStatus = null;
        Task.TaskPriority taskPriority = null;

        if (status != null && !status.isEmpty()) {
            try {
                taskStatus = Task.TaskStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid status value: " + status);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
        }

        if (priority != null && !priority.isEmpty()) {
            try {
                taskPriority = Task.TaskPriority.valueOf(priority);
            } catch (IllegalArgumentException e) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid priority value: " + priority);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
        }

        try {
            List<Task> tasks = taskService.filterTasks(taskStatus, taskPriority);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to filter tasks: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}