package com.shriva.jira_lite_backend_java.service;

import com.shriva.jira_lite_backend_java.entity.Project;
import com.shriva.jira_lite_backend_java.entity.Task;
import com.shriva.jira_lite_backend_java.entity.User;
import com.shriva.jira_lite_backend_java.repository.ProjectRepository;
import com.shriva.jira_lite_backend_java.repository.TaskRepository;
import com.shriva.jira_lite_backend_java.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public Task createTask(Long projectId, Task task, Long assignedToId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (assignedToId != null) {
            User assignedTo = userRepository.findById(assignedToId)
                    .orElseThrow(() -> new IllegalArgumentException("Assigned to user not found"));
            if (!assignedTo.getRole().equals("DEVELOPER") && !assignedTo.getRole().equals("TESTER")) {
                throw new IllegalArgumentException("Task can only be assigned to a DEVELOPER or TESTER");
            }
            task.setAssignedTo(assignedTo);
        }

        task.setProject(project);
        task.setCreatedAt(LocalDateTime.now());
        if (task.getStatus() == null) {
            task.setStatus(Task.TaskStatus.TO_DO);
        }
        return taskRepository.save(task);
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public Task updateTask(Long projectId, Long taskId, Task updatedTask, Long assignedToId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (!task.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Task does not belong to the specified project");
        }

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());

        if (assignedToId != null) {
            User assignedTo = userRepository.findById(assignedToId)
                    .orElseThrow(() -> new IllegalArgumentException("Assigned to user not found"));
            if (!assignedTo.getRole().equals("DEVELOPER") && !assignedTo.getRole().equals("TESTER")) {
                throw new IllegalArgumentException("Task can only be assigned to a DEVELOPER or TESTER");
            }
            task.setAssignedTo(assignedTo);
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long projectId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (!task.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Task does not belong to the specified project");
        }
        taskRepository.delete(task);
    }
}