import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import {
  LucideAngularModule,
  LayoutDashboard,
  Calendar,
  Clock,
  UserCircle,
  Users,
  Video,
  BarChart3,
} from 'lucide-angular';
import { AppointmentService, UserService, AuthService } from '../../services';
import { Appointment, UserProfileResponse } from '../../models';
import {
  SidebarComponent,
  MenuItem,
} from '../../components/layout/sidebar.component';

@Component({
  selector: 'app-doctor-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule, SidebarComponent],
  templateUrl: './doctor-dashboard.component.html',
  styleUrl: './doctor-dashboard.component.scss',
})
export class DoctorDashboardComponent implements OnInit {
  userProfile: UserProfileResponse | null = null;
  allAppointments: Appointment[] = [];
  todayAppointments: Appointment[] = [];
  pendingAppointments: Appointment[] = [];
  totalAppointments = 0;
  isOnBaseDashboard = true;

  menuItems: MenuItem[] = [
    { icon: LayoutDashboard, label: 'Dashboard', route: '/doctor/dashboard' },
    { icon: Calendar, label: 'Appointments', route: '/doctor/dashboard/appointments' },
    { icon: Clock, label: 'Schedule', route: '/doctor/dashboard/schedule' },
    { icon: Users, label: 'Patients', route: '/doctor/dashboard/patients' },
    { icon: Video, label: 'Video Queue', route: '/doctor/dashboard/video-queue' },
    { icon: BarChart3, label: 'Analytics', route: '/doctor/dashboard/analytics' },
    { icon: UserCircle, label: 'Profile', route: '/doctor/dashboard/profile' },
  ];

  constructor(
    private router: Router,
    private userService: UserService,
    private appointmentService: AppointmentService,
    private authService: AuthService
  ) {
    // Track current route to show/hide dashboard content
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.isOnBaseDashboard = event.urlAfterRedirects === '/doctor/dashboard';
      });
  }

  ngOnInit(): void {
    this.loadUserProfile();
    this.loadAppointments();
  }

  loadUserProfile(): void {
    this.userService.getCurrentUser().subscribe({
      next: (profile) => {
        this.userProfile = profile;
      },
      error: (error) => console.error('Failed to load profile', error),
    });
  }

  loadAppointments(): void {
    if (!this.userProfile?.id) return;

    this.appointmentService.getUserAppointments(this.userProfile.id).subscribe({
      next: (response) => {
        this.allAppointments = response.data;
        this.totalAppointments = response.data.length;

        const today = new Date();
        today.setHours(0, 0, 0, 0);

        this.todayAppointments = response.data.filter((apt) => {
          const aptDate = new Date(apt.appointmentTime!);
          aptDate.setHours(0, 0, 0, 0);
          return aptDate.getTime() === today.getTime();
        });

        this.pendingAppointments = response.data.filter(
          (apt) => apt.status === 'PENDING'
        );
      },
      error: (error) => console.error('Failed to load appointments', error),
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
