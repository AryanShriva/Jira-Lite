package com.shriva.jira_lite_backend_java.service;

import com.shriva.jira_lite_backend_java.entity.Task;
import com.shriva.jira_lite_backend_java.entity.User;
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
    private UserRepository userRepository;

    public Task createTask(Task task, Long assignedToId, Long assignedById) {
        User assignedTo = userRepository.findById(assignedToId)
                .orElseThrow(() -> new IllegalArgumentException("Assigned to user not found"));
        User assignedBy = userRepository.findById(assignedById)
                .orElseThrow(() -> new IllegalArgumentException("Assigned by user not found"));

        // Validate roles
        if (!assignedTo.getRole().equals("DEVELOPER") && !assignedTo.getRole().equals("TESTER")) {
            throw new IllegalArgumentException("Task can only be assigned to a DEVELOPER or TESTER");
        }
        if (!assignedBy.getRole().equals("ADMIN") && !assignedBy.getRole().equals("MANAGER")) {
            throw new IllegalArgumentException("Task can only be assigned by an ADMIN or MANAGER");
        }

        task.setAssignedTo(assignedTo);
        task.setAssignedBy(assignedBy);
        task.setCreatedAt(LocalDateTime.now());
        if (task.getStatus() == null) {
            task.setStatus(Task.TaskStatus.NOT_STARTED);
        }
        if (task.getPriority() == null) {
            task.setPriority(Task.TaskPriority.MEDIUM);
        }

        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    public Task updateTask(Long id, Task updatedTask, Long assignedToId, Long assignedById) {
        Task task = getTaskById(id);
        task.setName(updatedTask.getName());
        task.setDescription(updatedTask.getDescription());
        task.setEta(updatedTask.getEta());
        task.setStatus(updatedTask.getStatus());
        task.setPriority(updatedTask.getPriority());

        if (assignedToId != null) {
            User assignedTo = userRepository.findById(assignedToId)
                    .orElseThrow(() -> new IllegalArgumentException("Assigned to user not found"));
            if (!assignedTo.getRole().equals("DEVELOPER") && !assignedTo.getRole().equals("TESTER")) {
                throw new IllegalArgumentException("Task can only be assigned to a DEVELOPER or TESTER");
            }
            task.setAssignedTo(assignedTo);
        }

        if (assignedById != null) {
            User assignedBy = userRepository.findById(assignedById)
                    .orElseThrow(() -> new IllegalArgumentException("Assigned by user not found"));
            if (!assignedBy.getRole().equals("ADMIN") && !assignedBy.getRole().equals("MANAGER")) {
                throw new IllegalArgumentException("Task can only be assigned by an ADMIN or MANAGER");
            }
            task.setAssignedBy(assignedBy);
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    public List<Task> filterTasks(Task.TaskStatus status, Task.TaskPriority priority) {
        return taskRepository.findByStatusAndPriority(status, priority);
    }
}