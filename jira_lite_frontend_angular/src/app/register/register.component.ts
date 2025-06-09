import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClientModule, HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-register',
  standalone: true,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [
    CommonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    ReactiveFormsModule,
    HttpClientModule
  ]
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage: string | null = null;
  isLoading: boolean = false;
  roles = ['DEVELOPER', 'TESTER', 'MANAGER', 'ADMIN']; // Available roles from backend

  constructor(private fb: FormBuilder, private router: Router, private http: HttpClient) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.errorMessage = null;

      const { email, password, role } = this.registerForm.value;
      this.http.post<{ id: number, email: string, role: string }>(
        'http://localhost:8080/api/users',
        { email, password, role }
      ).subscribe({
        next: (response) => {
          this.isLoading = false;
          console.log('Registration successful, user:', response);
          alert('Registration successful! Please log in.');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = err.error?.error || 'An error occurred. Please try again.';
          console.error('Registration error:', err);
        }
      });
    }
  }
}