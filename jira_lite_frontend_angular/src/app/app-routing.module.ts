import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { AssignedTasksComponent } from './assigned-tasks/assigned-tasks.component';
import { UserManagementComponent } from './admin/user-management/user-management.component';
import { ProjectManagementComponent } from './manager/project-management/project-management.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'assigned-tasks', component: AssignedTasksComponent },
  { path: 'admin/users', component: UserManagementComponent },
  { path: 'manager/projects', component: ProjectManagementComponent },
  { path: '', pathMatch: 'full', children: [] },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }