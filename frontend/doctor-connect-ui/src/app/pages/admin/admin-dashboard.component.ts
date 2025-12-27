import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { LucideAngularModule, LayoutDashboard, UserCheck, Pill, FlaskConical, Users, Shield, Building2, DollarSign } from 'lucide-angular';
import { AdminService, AuthService } from '../../services';
import { AdminStats, DoctorProfile } from '../../models';
import { SidebarComponent, MenuItem } from '../../components/layout/sidebar.component';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule, SidebarComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss',
})
export class AdminDashboardComponent implements OnInit {
  stats: AdminStats | null = null;
  pendingDoctors: DoctorProfile[] = [];
  isOnBaseDashboard = true;

  menuItems: MenuItem[] = [
    { icon: LayoutDashboard, label: 'Dashboard', route: '/admin/dashboard' },
    { icon: UserCheck, label: 'Doctor Verification', route: '/admin/dashboard/doctor-verification', badge: 0 },
    { icon: Pill, label: 'Medicines', route: '/admin/dashboard/medicines' },
    { icon: FlaskConical, label: 'Lab Tests', route: '/admin/dashboard/lab-tests' },
    { icon: Users, label: 'Users', route: '/admin/dashboard/users' },
    { icon: Shield, label: 'Roles', route: '/admin/dashboard/roles' },
    { icon: Building2, label: 'Departments', route: '/admin/dashboard/departments' },
    { icon: DollarSign, label: 'Platform Earnings', route: '/admin/dashboard/platform-earnings' },
  ];

  constructor(
    private adminService: AdminService,
    private router: Router,
    private authService: AuthService
  ) {
    // Track navigation to determine if we're on base dashboard or child route
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        const isOnBase = event.urlAfterRedirects === '/admin/dashboard';

        // Reload data when navigating back to base dashboard
        if (isOnBase && !this.isOnBaseDashboard) {
          this.loadStats();
          this.loadPendingDoctors();
        }

        this.isOnBaseDashboard = isOnBase;
      });
  }

  ngOnInit(): void {
    // Check initial route
    this.isOnBaseDashboard = this.router.url === '/admin/dashboard';

    this.loadStats();
    this.loadPendingDoctors();
  }

  loadStats(): void {
    this.adminService.getSystemStats().subscribe({
      next: (stats) => {
        this.stats = stats;
      },
      error: (error) => console.error('Failed to load stats', error)
    });
  }

  loadPendingDoctors(): void {
    this.adminService.getPendingDoctorVerifications().subscribe({
      next: (response) => {
        this.pendingDoctors = response.data;
        // Update badge count for doctor verification
        const verificationMenuItem = this.menuItems.find(item => item.route === '/admin/dashboard/doctor-verification');
        if (verificationMenuItem) {
          verificationMenuItem.badge = this.pendingDoctors.length;
        }
      },
      error: (error) => console.error('Failed to load pending doctors', error)
    });
  }

  verifyDoctor(doctorId: string): void {
    this.adminService.verifyDoctor(doctorId).subscribe({
      next: () => {
        this.loadPendingDoctors();
      },
      error: (error) => console.error('Failed to verify doctor', error)
    });
  }

  rejectDoctor(doctorId: string): void {
    if (confirm('Are you sure you want to reject this doctor?')) {
      this.adminService.rejectDoctor(doctorId).subscribe({
        next: () => {
          this.loadPendingDoctors();
        },
        error: (error) => console.error('Failed to reject doctor', error)
      });
    }
  }

  onLogout(): void {
    this.authService.adminLogout().subscribe({
      next: () => {
        this.router.navigate(['/auth/admin-login']);
      },
      error: (error) => {
        console.error('Admin logout failed', error);
        // Navigate to admin login even if logout fails
        this.router.navigate(['/auth/admin-login']);
      },
    });
  }
}
