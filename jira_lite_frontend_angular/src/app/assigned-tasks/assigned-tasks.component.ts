import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { HttpClientModule, HttpClient, HttpHeaders } from '@angular/common/http';
import { Task } from './task.model';

@Component({
  selector: 'app-assigned-tasks',
  standalone: true,
  templateUrl: './assigned-tasks.component.html',
  styleUrls: ['./assigned-tasks.component.scss'],
  imports: [
    CommonModule,
    MatCardModule,
    MatListModule,
    HttpClientModule
  ]
})
export class AssignedTasksComponent implements OnInit {
  tasks: Task[] = [];
  user: { id: number, email: string, role: string } | null = null;
  errorMessage: string | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    // Get user data from localStorage
    const userData = localStorage.getItem('user');
    if (userData) {
      this.user = JSON.parse(userData);
      this.loadAssignedTasks();
    } else {
      this.errorMessage = 'User not found. Please log in again.';
    }
  }

  loadAssignedTasks() {
    if (!this.user) return;

    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    // Fetch tasks assigned to the user
    this.http.get<Task[]>(
      `http://localhost:8080/api/tasks?assigneeId=${this.user.id}`,
      { headers }
    ).subscribe({
      next: (tasks) => {
        this.tasks = tasks;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load tasks. Please try again.';
        console.error('Error loading tasks:', err);
      }
    });
  }
}