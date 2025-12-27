import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { LucideAngularModule, LayoutDashboard, Search, Calendar, FileText, Activity, UserCircle, Upload, CreditCard, Bell } from 'lucide-angular';
import {
  AppointmentService,
  NotificationService,
  UserService,
  AuthService,
} from '../../services';
import {
  Appointment,
  UserProfileResponse,
  AppointmentStatus,
} from '../../models';
import { SidebarComponent, MenuItem } from '../../components/layout/sidebar.component';

@Component({
  selector: 'app-patient-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule, SidebarComponent],
  templateUrl: './patient-dashboard.component.html',
  styleUrl: './patient-dashboard.component.scss',
})
export class PatientDashboardComponent implements OnInit {
  userProfile: UserProfileResponse | null = null;
  allAppointments: Appointment[] = [];
  upcomingAppointments: Appointment[] = [];
  unreadNotifications = 0;
  isOnBaseDashboard = true;

  menuItems: MenuItem[] = [
    { icon: LayoutDashboard, label: 'Dashboard', route: '/patient/dashboard' },
    { icon: Search, label: 'Find Doctors', route: '/patient/dashboard/search-doctors' },
    { icon: Calendar, label: 'My Appointments', route: '/patient/dashboard/appointments' },
    { icon: FileText, label: 'Prescriptions', route: '/patient/dashboard/prescriptions' },
    { icon: Activity, label: 'Medical History', route: '/patient/dashboard/medical-history' },
    { icon: Upload, label: 'Upload Reports', route: '/patient/dashboard/upload-reports' },
    { icon: CreditCard, label: 'Payment History', route: '/patient/dashboard/payment-history' },
    { icon: Bell, label: 'Notifications', route: '/patient/dashboard/notifications', badge: 0 },
    { icon: UserCircle, label: 'Profile', route: '/patient/dashboard/profile' },
  ];

  constructor(
    private router: Router,
    private userService: UserService,
    private appointmentService: AppointmentService,
    private notificationService: NotificationService,
    private authService: AuthService
  ) {
    // Track current route to show/hide dashboard content
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.isOnBaseDashboard = event.urlAfterRedirects === '/patient/dashboard';
      });
  }

  ngOnInit(): void {
    this.loadUserProfile();
    this.loadAppointments();
    this.loadNotifications();
  }

  loadUserProfile(): void {
    this.userService.getCurrentUser().subscribe({
      next: (profile) => {
        this.userProfile = profile;
        this.loadNotifications();
      },
      error: (error) => console.error('Failed to load profile', error),
    });
  }

  loadAppointments(): void {
    if (!this.userProfile?.id) {
      // Load profile first, then appointments
      this.userService.getCurrentUser().subscribe({
        next: (profile) => {
          this.userProfile = profile;
          if (profile.id) {
            this.fetchAppointments(profile.id);
          }
        },
        error: (error: any) => console.error('Failed to load profile', error),
      });
    } else {
      this.fetchAppointments(this.userProfile.id);
    }
  }

  private fetchAppointments(userId: string): void {
    this.appointmentService.getUserAppointments(userId).subscribe({
      next: (response) => {
        this.allAppointments = response.data;
        this.upcomingAppointments = response.data.filter(
          (apt: Appointment) =>
            apt.status === AppointmentStatus.CONFIRMED ||
            apt.status === AppointmentStatus.PENDING
        );
      },
      error: (error: any) =>
        console.error('Failed to load appointments', error),
    });
  }

  loadNotifications(): void {
    if (!this.userProfile?.id) return;

    this.notificationService.getUnreadCount(this.userProfile.id).subscribe({
      next: (response) => {
        this.unreadNotifications = response.count || 0;
        // Update badge count for notifications
        const notificationMenuItem = this.menuItems.find(item => item.route === '/patient/dashboard/notifications');
        if (notificationMenuItem) {
          notificationMenuItem.badge = this.unreadNotifications;
        }
      },
      error: (error) => console.error('Failed to load notifications', error),
    });
  }

  onLogout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/auth/login']);
      },
      error: (error) => {
        console.error('Logout failed', error);
        // Navigate to login even if logout fails
        this.router.navigate(['/auth/login']);
      },
    });
  }
}
