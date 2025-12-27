import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services';
import { RegisterRequest } from '../../models';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  fullName = '';
  email = '';
  phone = '';
  password = '';
  role = 'ROLE_PATIENT';
  licenseNumber = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const registerData: RegisterRequest = {
      emailOrPhoneNumber: this.email,
      password: this.password,
      fullName: this.fullName,
      phone: this.phone,
      email: this.email,
      roles: [{ name: this.role }],
      licenseNumber: this.role === 'ROLE_DOCTOR' ? this.licenseNumber : undefined,
    };

    this.authService.register(registerData).subscribe({
      next: (response) => {
        this.successMessage =
          'Account created successfully! Redirecting to login...';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        this.errorMessage =
          error.error?.message || 'Registration failed. Please try again.';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      },
    });
  }
}
