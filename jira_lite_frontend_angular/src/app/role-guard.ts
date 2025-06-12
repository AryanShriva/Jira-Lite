import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRoles = route.data['roles'] as string[];

    if (!requiredRoles) {
      return true; // No role restriction
    }

    const user = this.authService.getUser();
    if (!user || !requiredRoles.includes(user.role)) {
      // Redirect based on user role or lack thereof
      if (this.authService.isLoggedIn()) {
        this.authService.router.navigate(['/tasks']);
      } else {
        this.authService.logout();
      }
      return false;
    }

    return true;
  }
}