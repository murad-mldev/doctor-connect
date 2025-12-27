import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services';
import { AdminLoginRequest } from '../../models';

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-login.component.html',
  styleUrl: './admin-login.component.scss',
})
export class AdminLoginComponent {
  credentials: AdminLoginRequest = {
    username: '',
    password: ''
  };

  loading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';

    this.authService.adminLogin(this.credentials).subscribe({
      next: (response) => {
        console.log('Admin login response:', response);
        // Backend returns plain text, so any successful response means login succeeded
        this.router.navigate(['/admin/dashboard']);
        this.loading = false;
      },
      error: (error) => {
        console.error('Admin login error:', error);
        this.errorMessage = error.error || error.message || 'Admin login failed. Please check your credentials.';
        this.loading = false;
      }
    });
  }
}
