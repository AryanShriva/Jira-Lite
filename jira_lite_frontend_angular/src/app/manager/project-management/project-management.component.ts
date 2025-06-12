import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { HttpClientModule, HttpClient, HttpHeaders } from '@angular/common/http';
import { Project } from '../../project.model';

@Component({
  selector: 'app-project-management',
  standalone: true,
  templateUrl: './project-management.component.html',
  styleUrls: ['./project-management.component.scss'],
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    HttpClientModule
  ]
})
export class ProjectManagementComponent implements OnInit {
  projects: Project[] = [];
  displayedColumns: string[] = ['id', 'name', 'description', 'actions'];
  createProjectForm: FormGroup;
  updateProjectForm: FormGroup | null = null;
  errorMessage: string | null = null;

  constructor(
    private http: HttpClient,
    private fb: FormBuilder
  ) {
    this.createProjectForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadProjects();
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

  onCreateProject() {
    if (this.createProjectForm.valid) {
      const token = localStorage.getItem('authToken');
      const headers = new HttpHeaders({
        'Authorization': `Bearer ${token}`
      });

      this.http.post<Project>('http://localhost:8080/api/projects', this.createProjectForm.value, { headers }).subscribe({
        next: (newProject) => {
          this.projects.push(newProject);
          this.createProjectForm.reset();
          this.errorMessage = null;
        },
        error: (err) => {
          this.errorMessage = 'Failed to create project. Please try again.';
          console.error('Error creating project:', err);
        }
      });
    }
  }

  onEditProject(project: Project) {
    this.updateProjectForm = this.fb.group({
      id: [project.id, Validators.required],
      name: [project.name, Validators.required],
      description: [project.description, Validators.required]
    });
  }

  onUpdateProject() {
    if (this.updateProjectForm && this.updateProjectForm.valid) {
      const token = localStorage.getItem('authToken');
      const headers = new HttpHeaders({
        'Authorization': `Bearer ${token}`
      });

      const updatedProject = this.updateProjectForm.value;
      this.http.put<Project>(`http://localhost:8080/api/projects/${updatedProject.id}`, updatedProject, { headers }).subscribe({
        next: (response) => {
          const index = this.projects.findIndex(p => p.id === updatedProject.id);
          if (index !== -1) {
            this.projects[index] = response;
          }
          this.updateProjectForm = null;
          this.errorMessage = null;
        },
        error: (err) => {
          this.errorMessage = 'Failed to update project. Please try again.';
          console.error('Error updating project:', err);
        }
      });
    }
  }

  onDeleteProject(projectId: number) {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.delete(`http://localhost:8080/api/projects/${projectId}`, { headers }).subscribe({
      next: () => {
        this.projects = this.projects.filter(p => p.id !== projectId);
        this.errorMessage = null;
      },
      error: (err) => {
        this.errorMessage = 'Failed to delete project. Please try again.';
        console.error('Error deleting project:', err);
      }
    });
  }

  cancelEdit() {
    this.updateProjectForm = null;
  }
}