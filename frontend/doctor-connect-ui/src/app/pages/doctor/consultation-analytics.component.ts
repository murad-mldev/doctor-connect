import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, BarChart3, Users, Clock, Calendar, TrendingUp, Video } from 'lucide-angular';
import { VideoQueueService, DoctorStats } from '../../services/video-queue.service';
import { AppointmentService } from '../../services/appointment.service';
import { UserService } from '../../services/user.service';

interface MonthlyData {
  month: string;
  consultations: number;
  revenue: number;
}

@Component({
  selector: 'app-consultation-analytics',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './consultation-analytics.component.html',
  styleUrl: './consultation-analytics.component.scss'
})
export class ConsultationAnalyticsComponent implements OnInit {
  readonly BarChart3 = BarChart3;
  readonly Users = Users;
  readonly Clock = Clock;
  readonly Calendar = Calendar;
  readonly TrendingUp = TrendingUp;
  readonly Video = Video;

  stats: DoctorStats | null = null;
  appointments: any[] = [];
  monthlyData: MonthlyData[] = [];
  isLoading = false;
  error: string | null = null;

  // Calculated metrics
  totalRevenue = 0;
  completedAppointments = 0;
  cancelledAppointments = 0;
  upcomingAppointments = 0;

  constructor(
    private videoQueueService: VideoQueueService,
    private appointmentService: AppointmentService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadAnalytics();
  }

  loadAnalytics(): void {
    this.isLoading = true;
    this.error = null;

    // Load video consultation stats
    this.videoQueueService.getDoctorStats().subscribe({
      next: (stats) => {
        this.stats = stats;
      },
      error: (error) => {
        console.error('Error loading stats:', error);
      }
    });

    // Load appointments for analytics
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.appointmentService.getUserAppointments(user.id!).subscribe({
          next: (response) => {
            this.appointments = response.data;
            this.calculateMetrics();
            this.generateMonthlyData();
            this.isLoading = false;
          },
          error: (error) => {
            this.error = 'Failed to load analytics data';
            console.error('Error loading appointments:', error);
            this.isLoading = false;
          }
        });
      },
      error: (error) => {
        this.error = 'Failed to load user information';
        console.error('Error loading user:', error);
        this.isLoading = false;
      }
    });
  }

  calculateMetrics(): void {
    this.completedAppointments = this.appointments.filter(
      (apt: any) => apt.status === 'COMPLETED'
    ).length;

    this.cancelledAppointments = this.appointments.filter(
      (apt: any) => apt.status === 'CANCELLED'
    ).length;

    this.upcomingAppointments = this.appointments.filter(
      (apt: any) => apt.status === 'CONFIRMED' && new Date(apt.appointmentDate) > new Date()
    ).length;

    // Calculate total revenue from completed appointments
    this.totalRevenue = this.appointments
      .filter((apt: any) => apt.status === 'COMPLETED')
      .reduce((sum: number, apt: any) => sum + (apt.fee || 0), 0);
  }

  generateMonthlyData(): void {
    const monthMap = new Map<string, { consultations: number; revenue: number }>();

    this.appointments.forEach((apt: any) => {
      if (apt.status === 'COMPLETED') {
        const date = new Date(apt.appointmentDate);
        const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;

        if (!monthMap.has(monthKey)) {
          monthMap.set(monthKey, { consultations: 0, revenue: 0 });
        }

        const data = monthMap.get(monthKey)!;
        data.consultations++;
        data.revenue += apt.fee || 0;
      }
    });

    // Convert to array and sort by month
    this.monthlyData = Array.from(monthMap.entries())
      .map(([month, data]) => ({
        month,
        consultations: data.consultations,
        revenue: data.revenue
      }))
      .sort((a, b) => a.month.localeCompare(b.month))
      .slice(-6); // Last 6 months
  }

  formatMonth(monthKey: string): string {
    const [year, month] = monthKey.split('-');
    const date = new Date(parseInt(year), parseInt(month) - 1);
    return date.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
  }

  getMaxConsultations(): number {
    if (this.monthlyData.length === 0) return 1;
    return Math.max(...this.monthlyData.map(d => d.consultations), 1);
  }

  getMaxRevenue(): number {
    if (this.monthlyData.length === 0) return 1;
    return Math.max(...this.monthlyData.map(d => d.revenue), 1);
  }
}
