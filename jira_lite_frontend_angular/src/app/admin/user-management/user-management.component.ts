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
import { User } from '../../user.model';

@Component({
  selector: 'app-user-management',
  standalone: true,
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss'],
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
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  displayedColumns: string[] = ['id', 'email', 'role', 'actions'];
  createUserForm: FormGroup;
  updateUserForm: FormGroup | null = null;
  errorMessage: string | null = null;
  roles = ['DEVELOPER', 'TESTER', 'MANAGER', 'ADMIN'];

  constructor(
    private http: HttpClient,
    private fb: FormBuilder
  ) {
    this.createUserForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadUsers();
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

  onCreateUser() {
    if (this.createUserForm.valid) {
      const token = localStorage.getItem('authToken');
      const headers = new HttpHeaders({
        'Authorization': `Bearer ${token}`
      });

      this.http.post<User>('http://localhost:8080/api/users', this.createUserForm.value, { headers }).subscribe({
        next: (newUser) => {
          this.users.push(newUser);
          this.createUserForm.reset();
          this.errorMessage = null;
        },
        error: (err) => {
          this.errorMessage = 'Failed to create user. Please try again.';
          console.error('Error creating user:', err);
        }
      });
    }
  }

  onEditUser(user: User) {
    this.updateUserForm = this.fb.group({
      id: [user.id, Validators.required],
      email: [user.email, [Validators.required, Validators.email]],
      role: [user.role, Validators.required]
    });
  }

  onUpdateUser() {
    if (this.updateUserForm && this.updateUserForm.valid) {
      const token = localStorage.getItem('authToken');
      const headers = new HttpHeaders({
        'Authorization': `Bearer ${token}`
      });

      const updatedUser = this.updateUserForm.value;
      this.http.put<User>(`http://localhost:8080/api/users/${updatedUser.id}`, updatedUser, { headers }).subscribe({
        next: (response) => {
          const index = this.users.findIndex(u => u.id === updatedUser.id);
          if (index !== -1) {
            this.users[index] = response;
          }
          this.updateUserForm = null;
          this.errorMessage = null;
        },
        error: (err) => {
          this.errorMessage = 'Failed to update user. Please try again.';
          console.error('Error updating user:', err);
        }
      });
    }
  }

  onDeleteUser(userId: number) {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.delete(`http://localhost:8080/api/users/${userId}`, { headers }).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== userId);
        this.errorMessage = null;
      },
      error: (err) => {
        this.errorMessage = 'Failed to delete user. Please try again.';
        console.error('Error deleting user:', err);
      }
    });
  }

  cancelEdit() {
    this.updateUserForm = null;
  }
}