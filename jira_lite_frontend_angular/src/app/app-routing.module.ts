import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { AssignedTasksComponent } from './assigned-tasks/assigned-tasks.component';
import { UserManagementComponent } from './admin/user-management/user-management.component';
import { ProjectManagementComponent } from './manager/project-management/project-management.component';
import { TaskManagementComponent } from './manager/task-management/task-management.component';

import { AuthGuard } from './auth-guard';
import { RoleGuard } from './role-guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { 
    path: 'tasks', 
    component: AssignedTasksComponent, 
    canActivate: [AuthGuard] // Requires login
  },
  { 
    path: 'assigned-tasks', 
    component: AssignedTasksComponent, 
    canActivate: [AuthGuard] // Requires login
  },
  { 
    path: 'admin/users', 
    component: UserManagementComponent, 
    canActivate: [AuthGuard, RoleGuard], 
    data: { roles: ['ADMIN'] } // Requires ADMIN role
  },
  { 
    path: 'manager/projects', 
    component: ProjectManagementComponent, 
    canActivate: [AuthGuard, RoleGuard], 
    data: { roles: ['MANAGER', 'ADMIN'] } // Requires MANAGER or ADMIN role
  },
  { 
    path: 'manager/tasks', 
    component: TaskManagementComponent, 
    canActivate: [AuthGuard, RoleGuard], 
    data: { roles: ['MANAGER', 'ADMIN'] } // Requires MANAGER or ADMIN role
  },
  { path: '', pathMatch: 'full', children: [] },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }