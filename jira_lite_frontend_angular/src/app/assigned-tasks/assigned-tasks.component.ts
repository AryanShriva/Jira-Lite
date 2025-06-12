import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { HttpClientModule, HttpClient, HttpHeaders } from '@angular/common/http';
import { Task } from '../task.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-assigned-tasks',
  standalone: true,
  templateUrl: './assigned-tasks.component.html',
  styleUrls: ['./assigned-tasks.component.scss'],
  imports: [
    CommonModule,
    MatCardModule,
    MatListModule,
    MatButtonModule,
    MatSelectModule,
    MatFormFieldModule,
    HttpClientModule,
    FormsModule
  ]
})
export class AssignedTasksComponent implements OnInit {
  tasks: Task[] = [];
  filteredTasks: Task[] = [];
  user: { id: number, email: string, role: string } | null = null;
  errorMessage: string | null = null;
  selectedTask: Task | null = null;
  statuses = ['To Do', 'In Progress', 'Done'];
  selectedStatus: string = 'All';
  sortField: 'title' | 'projectId' | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';

  constructor(private http: HttpClient) {
    const userData = localStorage.getItem('user');
    if (userData) {
      this.user = JSON.parse(userData);
    }
  }

  ngOnInit() {
    this.loadAssignedTasks();
  }

  loadAssignedTasks() {
    if (!this.user) return;

    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<Task[]>(
      `http://localhost:8080/api/tasks?assigneeId=${this.user.id}`,
      { headers }
    ).subscribe({
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

  selectTask(task: Task) {
    this.selectedTask = { ...task };
  }

  updateTaskStatus() {
    if (!this.selectedTask) return;

    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.put<Task>(
      `http://localhost:8080/api/tasks/${this.selectedTask.id}`,
      this.selectedTask,
      { headers }
    ).subscribe({
      next: (updatedTask) => {
        const index = this.tasks.findIndex(t => t.id === updatedTask.id);
        if (index !== -1) {
          this.tasks[index] = updatedTask;
        }
        this.selectedTask = null;
        this.errorMessage = null;
        this.applyFiltersAndSorting();
      },
      error: (err) => {
        this.errorMessage = 'Failed to update task status. Please try again.';
        console.error('Error updating task:', err);
      }
    });
  }

  cancelEdit() {
    this.selectedTask = null;
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