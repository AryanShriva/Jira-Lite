import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClientModule, HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [
    CommonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    ReactiveFormsModule,
    HttpClientModule
  ]
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string | null = null;
  isLoading: boolean = false;
  showRegisterButton: boolean = false; // Flag to show Register button

  constructor(private fb: FormBuilder, private router: Router, private http: HttpClient) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = null;
      this.showRegisterButton = false;

      const { email, password } = this.loginForm.value;
      this.http.post<{ token: string, user: { id: number, email: string, role: string } }>(
        'http://localhost:8080/api/auth/login',
        { email, password }
      ).subscribe({
        next: (response) => {
          this.isLoading = false;
          localStorage.setItem('authToken', response.token);
          localStorage.setItem('user', JSON.stringify(response.user));
          console.log('Login successful, token:', response.token, 'user:', response.user);
          this.router.navigate(['/assigned-tasks']); // Redirect to assigned tasks dashboard
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = err.error?.error || 'An error occurred. Please try again.';
          if (this.errorMessage && this.errorMessage.includes('Invalid credentials')) {
            this.showRegisterButton = true; // Show Register button if user not found
          }
          console.error('Login error:', err);
        }
      });
    }
  }

  onRegister() {
    this.router.navigate(['/register']); // Navigate to registration page
  }
}