package com.shriva.jira_lite_backend_java.service;

import com.shriva.jira_lite_backend_java.entity.Project;
import com.shriva.jira_lite_backend_java.entity.User;
import com.shriva.jira_lite_backend_java.repository.ProjectRepository;
import com.shriva.jira_lite_backend_java.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public Project createProject(Project project, Long assignedToId, Long assignedById) {
        User assignedTo = userRepository.findById(assignedToId)
                .orElseThrow(() -> new IllegalArgumentException("Assigned to user not found"));
        User assignedBy = userRepository.findById(assignedById)
                .orElseThrow(() -> new IllegalArgumentException("Assigned by user not found"));

        // Validate roles
        if (!assignedTo.getRole().equals("DEVELOPER") && !assignedTo.getRole().equals("TESTER")) {
            throw new IllegalArgumentException("Project can only be assigned to a DEVELOPER or TESTER");
        }
        if (!assignedBy.getRole().equals("ADMIN") && !assignedBy.getRole().equals("MANAGER")) {
            throw new IllegalArgumentException("Project can only be assigned by an ADMIN or MANAGER");
        }

        project.setAssignedTo(assignedTo);
        project.setAssignedBy(assignedBy);
        project.setCreatedAt(LocalDateTime.now());
        if (project.getStatus() == null) {
            project.setStatus(Project.ProjectStatus.NOT_STARTED);
        }
        if (project.getPriority() == null) {
            project.setPriority(Project.ProjectPriority.MEDIUM);
        }

        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    public Project updateProject(Long id, Project updatedProject, Long assignedToId, Long assignedById) {
        Project project = getProjectById(id);
        project.setName(updatedProject.getName());
        project.setDescription(updatedProject.getDescription());
        project.setEta(updatedProject.getEta());
        project.setStatus(updatedProject.getStatus());
        project.setPriority(updatedProject.getPriority());

        if (assignedToId != null) {
            User assignedTo = userRepository.findById(assignedToId)
                    .orElseThrow(() -> new IllegalArgumentException("Assigned to user not found"));
            if (!assignedTo.getRole().equals("DEVELOPER") && !assignedTo.getRole().equals("TESTER")) {
                throw new IllegalArgumentException("Project can only be assigned to a DEVELOPER or TESTER");
            }
            project.setAssignedTo(assignedTo);
        }

        if (assignedById != null) {
            User assignedBy = userRepository.findById(assignedById)
                    .orElseThrow(() -> new IllegalArgumentException("Assigned by user not found"));
            if (!assignedBy.getRole().equals("ADMIN") && !assignedBy.getRole().equals("MANAGER")) {
                throw new IllegalArgumentException("Project can only be assigned by an ADMIN or MANAGER");
            }
            project.setAssignedBy(assignedBy);
        }

        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        projectRepository.delete(project);
    }

    public List<Project> filterProjects(Project.ProjectStatus status, Project.ProjectPriority priority) {
        return projectRepository.findByStatusAndPriority(status, priority);
    }
}