import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { CanActivateFn } from '@angular/router';
import { UserService } from '../services';
import { map, catchError, of } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const userService = inject(UserService);

  // For session-based auth, verify by checking if we can get the current user profile
  return userService.getCurrentUser().pipe(
    map(profile => {
      if (profile && profile.id) {
        return true;
      }
      router.navigate(['/login']);
      return false;
    }),
    catchError(() => {
      router.navigate(['/login']);
      return of(false);
    })
  );
};

export const roleGuard: (allowedRoles: string[]) => CanActivateFn = (allowedRoles: string[]) => {
  return (route, state) => {
    const router = inject(Router);
    const userService = inject(UserService);

    // For session-based auth, verify by checking current user's roles
    return userService.getCurrentUser().pipe(
      map(profile => {
        if (!profile || !profile.id) {
          router.navigate(['/login']);
          return false;
        }

        const userRoles = profile.roles?.map(r => r.name) || [];
        const hasAllowedRole = allowedRoles.some(role => userRoles.includes(role));

        if (hasAllowedRole) {
          return true;
        }

        router.navigate(['/login']);
        return false;
      }),
      catchError(() => {
        router.navigate(['/login']);
        return of(false);
      })
    );
  };
};
