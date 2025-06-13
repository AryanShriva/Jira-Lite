import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { HttpClientModule, HttpClient, HttpHeaders } from '@angular/common/http';
import { Project } from '../../project.model';
import { Task } from '../../task.model';

interface ProjectSummary {
  project: Project;
  totalTasks: number;
  tasksToDo: number;
  tasksInProgress: number;
  tasksDone: number;
  completionPercentage: number;
}

@Component({
  selector: 'app-project-dashboard',
  standalone: true,
  templateUrl: './project-dashboard.component.html',
  styleUrls: ['./project-dashboard.component.scss'],
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressBarModule,
    HttpClientModule
  ]
})
export class ProjectDashboardComponent implements OnInit {
  projects: Project[] = [];
  tasks: Task[] = [];
  projectSummaries: ProjectSummary[] = [];
  errorMessage: string | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadProjects();
    this.loadTasks();
  }

  loadProjects() {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<Project[]>('http://localhost:8080/api/projects', { headers }).subscribe({
      next: (projects) => {
        this.projects = projects;
        this.calculateSummaries();
      },
      error: (err) => {
        this.errorMessage = 'Failed to load projects. Please try again.';
        console.error('Error loading projects:', err);
      }
    });
  }

  loadTasks() {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<Task[]>('http://localhost:8080/api/tasks', { headers }).subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.calculateSummaries();
      },
      error: (err) => {
        this.errorMessage = 'Failed to load tasks. Please try again.';
        console.error('Error loading tasks:', err);
      }
    });
  }

  calculateSummaries() {
    if (!this.projects.length || !this.tasks.length) return;

    this.projectSummaries = this.projects.map(project => {
      const projectTasks = this.tasks.filter(task => task.projectId === project.id);
      const totalTasks = projectTasks.length;
      const tasksToDo = projectTasks.filter(task => task.status === 'To Do').length;
      const tasksInProgress = projectTasks.filter(task => task.status === 'In Progress').length;
      const tasksDone = projectTasks.filter(task => task.status === 'Done').length;
      const completionPercentage = totalTasks > 0 ? (tasksDone / totalTasks) * 100 : 0;

      return {
        project,
        totalTasks,
        tasksToDo,
        tasksInProgress,
        tasksDone,
        completionPercentage: Math.round(completionPercentage)
      };
    });
  }
}