import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, Bell, CheckCircle, Calendar, FileText, Video, CreditCard } from 'lucide-angular';
import { NotificationService } from '../../services/notification.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.scss'
})
export class NotificationsComponent implements OnInit {
  readonly Bell = Bell;
  readonly CheckCircle = CheckCircle;
  readonly Calendar = Calendar;
  readonly FileText = FileText;
  readonly Video = Video;
  readonly CreditCard = CreditCard;

  notifications: any[] = [];
  isLoading = false;
  error: string | null = null;

  constructor(
    private notificationService: NotificationService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.isLoading = true;

    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.notificationService.getUserNotifications(user.id!).subscribe({
          next: (response) => {
            this.notifications = response.data;
            this.isLoading = false;
          },
          error: (error) => {
            this.error = 'Failed to load notifications';
            this.isLoading = false;
          }
        });
      }
    });
  }

  markAsRead(notificationId: string): void {
    this.notificationService.markAsRead(notificationId).subscribe({
      next: () => {
        const notification = this.notifications.find(n => n.id === notificationId);
        if (notification) {
          notification.read = true;
        }
      },
      error: (error) => console.error('Error marking as read:', error)
    });
  }

  getNotificationIcon(type: string): any {
    const iconMap: { [key: string]: any } = {
      'APPOINTMENT_REMINDER': this.Calendar,
      'APPOINTMENT_CONFIRMED': this.CheckCircle,
      'PRESCRIPTION_READY': this.FileText,
      'VIDEO_CALL_READY': this.Video,
      'PAYMENT_RECEIVED': this.CreditCard,
      'DEFAULT': this.Bell
    };
    return iconMap[type] || iconMap['DEFAULT'];
  }

  getNotificationClass(type: string): string {
    const classMap: { [key: string]: string } = {
      'APPOINTMENT_REMINDER': 'notification-warning',
      'APPOINTMENT_CONFIRMED': 'notification-success',
      'PRESCRIPTION_READY': 'notification-info',
      'VIDEO_CALL_READY': 'notification-video',
      'PAYMENT_RECEIVED': 'notification-success',
      'DEFAULT': 'notification-default'
    };
    return classMap[type] || classMap['DEFAULT'];
  }
}
