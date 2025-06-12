import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { HttpClientModule, HttpClient, HttpHeaders } from '@angular/common/http';
import { Task } from '../../task.model';
import { User } from '../../user.model';
import { Project } from '../../project.model';

@Component({
  selector: 'app-task-management',
  standalone: true,
  templateUrl: './task-management.component.html',
  styleUrls: ['./task-management.component.scss'],
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule,
    HttpClientModule
  ]
})
export class TaskManagementComponent implements OnInit {
  tasks: Task[] = [];
  filteredTasks: Task[] = []; // For displaying filtered/sorted tasks
  users: User[] = [];
  projects: Project[] = [];
  displayedColumns: string[] = ['id', 'title', 'description', 'status', 'projectId', 'assigneeId', 'actions'];
  createTaskForm: FormGroup;
  updateTaskForm: FormGroup | null = null;
  errorMessage: string | null = null;
  statuses = ['To Do', 'In Progress', 'Done'];
  selectedStatus: string = 'All'; // Default filter
  sortField: 'title' | 'projectId' | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';

  constructor(
    private http: HttpClient,
    private fb: FormBuilder
  ) {
    this.createTaskForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      status: ['To Do', Validators.required],
      projectId: [null, Validators.required],
      assigneeId: [null, Validators.required]
    });
  }

  ngOnInit() {
    this.loadUsers();
    this.loadProjects();
    this.loadTasks();
  }

  loadUsers() {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<User[]>('http://localhost:8080/api/users', { headers }).subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load users. Please try again.';
        console.error('Error loading users:', err);
      }
    });
  }

  loadProjects() {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<Project[]>('http://localhost:8080/api/projects', { headers }).subscribe({
      next: (projects) => {
        this.projects = projects;
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
        this.applyFiltersAndSorting();
      },
      error: (err) => {
        this.errorMessage = 'Failed to load tasks. Please try again.';
        console.error('Error loading tasks:', err);
      }
    });
  }

  onCreateTask() {
    if (this.createTaskForm.valid) {
      const token = localStorage.getItem('authToken');
      const headers = new HttpHeaders({
        'Authorization': `Bearer ${token}`
      });

      this.http.post<Task>('http://localhost:8080/api/tasks', this.createTaskForm.value, { headers }).subscribe({
        next: (newTask) => {
          this.tasks.push(newTask);
          this.createTaskForm.reset({ status: 'To Do' });
          this.errorMessage = null;
          this.applyFiltersAndSorting();
        },
        error: (err) => {
          this.errorMessage = 'Failed to create task. Please try again.';
          console.error('Error creating task:', err);
        }
      });
    }
  }

  onEditTask(task: Task) {
    this.updateTaskForm = this.fb.group({
      id: [task.id, Validators.required],
      title: [task.title, Validators.required],
      description: [task.description, Validators.required],
      status: [task.status, Validators.required],
      projectId: [task.projectId, Validators.required],
      assigneeId: [task.assigneeId, Validators.required]
    });
  }

  onUpdateTask() {
    if (this.updateTaskForm && this.updateTaskForm.valid) {
      const token = localStorage.getItem('authToken');
      const headers = new HttpHeaders({
        'Authorization': `Bearer ${token}`
      });

      const updatedTask = this.updateTaskForm.value;
      this.http.put<Task>(`http://localhost:8080/api/tasks/${updatedTask.id}`, updatedTask, { headers }).subscribe({
        next: (response) => {
          const index = this.tasks.findIndex(t => t.id === updatedTask.id);
          if (index !== -1) {
            this.tasks[index] = response;
          }
          this.updateTaskForm = null;
          this.errorMessage = null;
          this.applyFiltersAndSorting();
        },
        error: (err) => {
          this.errorMessage = 'Failed to update task. Please try again.';
          console.error('Error updating task:', err);
        }
      });
    }
  }

  onDeleteTask(taskId: number) {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.delete(`http://localhost:8080/api/tasks/${taskId}`, { headers }).subscribe({
      next: () => {
        this.tasks = this.tasks.filter(t => t.id !== taskId);
        this.errorMessage = null;
        this.applyFiltersAndSorting();
      },
      error: (err) => {
        this.errorMessage = 'Failed to delete task. Please try again.';
        console.error('Error deleting task:', err);
      }
    });
  }

  cancelEdit() {
    this.updateTaskForm = null;
  }

  filterTasks(status: string) {
    this.selectedStatus = status;
    this.applyFiltersAndSorting();
  }

  sortTasks(field: 'title' | 'projectId') {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
    this.applyFiltersAndSorting();
  }

  applyFiltersAndSorting() {
    let tasks = [...this.tasks];

    // Apply status filter
    if (this.selectedStatus !== 'All') {
      tasks = tasks.filter(task => task.status === this.selectedStatus);
    }

    // Apply sorting
    if (this.sortField) {
      tasks.sort((a, b) => {
        let comparison = 0;
        if (this.sortField === 'title') {
          comparison = a.title.localeCompare(b.title);
        } else if (this.sortField === 'projectId') {
          comparison = a.projectId - b.projectId;
        }
        return this.sortDirection === 'asc' ? comparison : -comparison;
      });
    }

    this.filteredTasks = tasks;
  }
}