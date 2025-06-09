import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  imports: [
    CommonModule,
    RouterOutlet,
    MatButtonModule,
    MatCardModule
  ]
})
export class AppComponent {
  title = 'jira_lite_frontend_angular';

  constructor(private router: Router) {}

  onGetStarted() {
    console.log('Get Started button clicked');
    // Check if user is logged in by checking for authToken in localStorage
    const authToken = localStorage.getItem('authToken');
    if (authToken) {
      console.log('User is logged in, redirecting to assigned tasks dashboard');
      this.router.navigate(['/assigned-tasks']);
    } else {
      this.router.navigate(['/login']);
    }
  }
}