import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../services';
import { UserService } from '../../services/user.service';
import { LoginRequest } from '../../models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, TranslateModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  credentials: LoginRequest = {
    emailOrPhoneNumber: '',
    password: '',
  };

  loading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {}

  getTranslatedError(): string {
    return 'Login failed. Please try again.';
  }

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        // Session-based auth - no token storage
        // Backend handles session via cookies
        if (response.success) {
          // Get user profile to determine role and redirect accordingly
          this.userService.getCurrentUser().subscribe({
            next: (userProfile) => {
              // Check user roles and redirect based on role
              if (userProfile.roles && userProfile.roles.length > 0) {
                const role = userProfile.roles[0].name.toUpperCase();
                console.log('User role:', role);
                switch (role) {
                  case 'ROLE_PATIENT':
                    this.router.navigate(['/patient/dashboard']);
                    break;
                  case 'ROLE_USER':
                    this.router.navigate(['/patient/dashboard']);
                    break;
                  case 'ROLE_DOCTOR':
                    this.router.navigate(['/doctor/dashboard']);
                    break;
                  case 'ROLE_ADMIN':
                    this.router.navigate(['/admin/dashboard']);
                    break;
                  default:
                    // Default to patient dashboard if role is unknown
                    this.router.navigate(['/patient/dashboard']);
                }
              } else {
                // No roles found, default to patient dashboard
                this.router.navigate(['/patient/dashboard']);
              }
              this.loading = false;
            },
            error: (error) => {
              this.errorMessage =
                'Failed to load user profile. Please try again.';
              this.loading = false;
            },
          });
        } else {
          this.errorMessage = response.message || this.getTranslatedError();
          this.loading = false;
        }
      },
      error: (error) => {
        this.errorMessage = error.error?.message || this.getTranslatedError();
        this.loading = false;
      },
    });
  }
}
