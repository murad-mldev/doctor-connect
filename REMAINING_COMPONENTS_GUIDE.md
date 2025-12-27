# Remaining Components Implementation Guide

## Components Created Today ✅

### Admin Components
1. ✅ **departments.component** - Manage hospital departments (CRUD with grid view)

### Patient Components
2. ✅ **upload-reports.component** - Medical reports upload with drag-drop

### Shared Components
3. ✅ **file-upload.component** - Reusable file upload with drag-drop

### Doctor Components (Partial)
4. ⚠️ **patients-list.component** - Started (TS file created, needs HTML/SCSS)

---

## Critical Components Still Needed (Continue from here)

### For Doctor Pages:

#### doctor/patients-list.component.html
```html
<div class="page-container">
  <div class="page-header">
    <h1>My Patients</h1>
    <p>View your assigned patients and their medical history</p>
  </div>

  <div class="error-alert" *ngIf="error">
    <p>{{ error }}</p>
  </div>

  <div class="content-card" *ngIf="isLoading">
    <div class="loading-state">
      <p>Loading patients...</p>
    </div>
  </div>

  <div class="content-card" *ngIf="!isLoading && patients.length > 0">
    <table class="patients-table">
      <thead>
        <tr>
          <th>Patient Name</th>
          <th>Last Visit</th>
          <th>Total Visits</th>
          <th>Upcoming</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let patient of patients">
          <td>
            <div class="patient-info">
              <lucide-icon [img]="Users" [size]="20"></lucide-icon>
              <span>{{ patient.patientName }}</span>
            </div>
          </td>
          <td>{{ patient.lastVisit | date:'medium' }}</td>
          <td>{{ patient.totalVisits }}</td>
          <td>
            <span class="badge">{{ patient.upcomingAppointments }}</span>
          </td>
          <td>
            <button class="btn-icon" (click)="viewMedicalHistory(patient)" title="View Medical History">
              <lucide-icon [img]="FileText" [size]="18"></lucide-icon>
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>

  <div class="content-card" *ngIf="!isLoading && patients.length === 0">
    <div class="empty-state">
      <lucide-icon [img]="Users" [size]="48"></lucide-icon>
      <h3>No Patients Yet</h3>
      <p>Patients will appear here once they book appointments with you</p>
    </div>
  </div>
</div>

<!-- Medical History Modal -->
<div class="modal-overlay" *ngIf="showMedicalHistoryModal" (click)="closeMedicalHistoryModal()">
  <div class="modal-content large" (click)="$event.stopPropagation()">
    <div class="modal-header">
      <h2>Medical History - {{ selectedPatient?.patientName }}</h2>
      <button class="btn-icon" (click)="closeMedicalHistoryModal()">×</button>
    </div>
    <div class="modal-body">
      <div *ngIf="loadingHistory">Loading medical history...</div>
      <div *ngIf="!loadingHistory && medicalHistory">
        <app-medical-history-display [history]="medicalHistory"></app-medical-history-display>
      </div>
    </div>
  </div>
</div>
```

#### doctor/patients-list.component.scss
Use similar styles to roles.component.scss with table styling.

---

### For Doctor Video Queue:

#### doctor/video-queue.component.ts
```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, Video, Users, Play, Clock } from 'lucide-angular';
import { VideoQueueService } from '../../services/video-queue.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-video-queue',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './video-queue.component.html',
  styleUrl: './video-queue.component.scss'
})
export class VideoQueueComponent implements OnInit, OnDestroy {
  readonly Video = Video;
  readonly Users = Users;
  readonly Play = Play;
  readonly Clock = Clock;

  queue: any = null;
  waitingPatients: any[] = [];
  isLoading = false;
  error: string | null = null;
  refreshInterval: any;

  constructor(
    private videoQueueService: VideoQueueService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadQueue();
    // Auto-refresh every 10 seconds
    this.refreshInterval = setInterval(() => this.loadQueue(), 10000);
  }

  ngOnDestroy(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }

  loadQueue(): void {
    this.videoQueueService.getMyQueue().subscribe({
      next: (queue) => {
        this.queue = queue;
      },
      error: (error) => {
        this.error = 'Failed to load queue';
        console.error('Error:', error);
      }
    });

    this.videoQueueService.getWaitingPatients().subscribe({
      next: (patients) => {
        this.waitingPatients = patients;
      },
      error: (error) => console.error('Error loading patients:', error)
    });
  }

  startConsultation(appointmentId: string): void {
    this.videoQueueService.startConsultation(appointmentId).subscribe({
      next: (response) => {
        // Navigate to video room with access token
        this.router.navigate(['/video-consultation', appointmentId], {
          queryParams: { token: response.accessToken }
        });
      },
      error: (error) => {
        this.error = 'Failed to start consultation';
        console.error('Error:', error);
      }
    });
  }
}
```

---

### For Patient Video Waiting Room:

#### patient/video-waiting-room.component.ts
```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { LucideAngularModule, Video, Clock, Users } from 'lucide-angular';
import { VideoQueueService } from '../../services/video-queue.service';

@Component({
  selector: 'app-video-waiting-room',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './video-waiting-room.component.html',
  styleUrl: './video-waiting-room.component.scss'
})
export class VideoWaitingRoomComponent implements OnInit, OnDestroy {
  readonly Video = Video;
  readonly Clock = Clock;
  readonly Users = Users;

  appointmentId: string | null = null;
  queuePosition: any = null;
  isLoading = false;
  error: string | null = null;
  checkInterval: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private videoQueueService: VideoQueueService
  ) {}

  ngOnInit(): void {
    this.appointmentId = this.route.snapshot.paramMap.get('appointmentId');

    if (this.appointmentId) {
      this.joinWaitingRoom();
      // Check position every 5 seconds
      this.checkInterval = setInterval(() => this.checkPosition(), 5000);
    }
  }

  ngOnDestroy(): void {
    if (this.checkInterval) {
      clearInterval(this.checkInterval);
    }
  }

  joinWaitingRoom(): void {
    this.videoQueueService.joinWaitingRoom(this.appointmentId!).subscribe({
      next: (response) => {
        this.queuePosition = response;
        this.checkPosition();
      },
      error: (error) => {
        this.error = 'Failed to join waiting room';
        console.error('Error:', error);
      }
    });
  }

  checkPosition(): void {
    this.videoQueueService.getMyQueuePosition(this.appointmentId!).subscribe({
      next: (position) => {
        this.queuePosition = position;

        // If it's our turn (position 1 or status indicates ready)
        if (position.position === 1 || position.status === 'READY') {
          this.joinConsultation();
        }
      },
      error: (error) => console.error('Error checking position:', error)
    });
  }

  joinConsultation(): void {
    this.videoQueueService.joinConsultation(this.appointmentId!).subscribe({
      next: (response) => {
        // Navigate to video room
        this.router.navigate(['/video-consultation', this.appointmentId], {
          queryParams: { token: response.accessToken }
        });
      },
      error: (error) => {
        this.error = 'Failed to join consultation';
        console.error('Error:', error);
      }
    });
  }
}
```

---

### For Patient Notifications:

#### patient/notifications.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, Bell, CheckCircle } from 'lucide-angular';
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
          next: (notifications) => {
            this.notifications = notifications;
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
}
```

---

### For Patient Payment History:

#### patient/payment-history.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, CreditCard, Download } from 'lucide-angular';
import { PaymentService } from '../../services/payment.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-payment-history',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './payment-history.component.html',
  styleUrl: './payment-history.component.scss'
})
export class PaymentHistoryComponent implements OnInit {
  readonly CreditCard = CreditCard;
  readonly Download = Download;

  payments: any[] = [];
  isLoading = false;
  error: string | null = null;

  constructor(
    private paymentService: PaymentService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadPayments();
  }

  loadPayments(): void {
    this.isLoading = true;
    // TODO: Add getPaymentsByUser endpoint
    // For now, showing placeholder
    this.error = 'Payment history feature coming soon';
    this.isLoading = false;
  }

  downloadReceipt(paymentId: string): void {
    console.log('Download receipt for payment:', paymentId);
    // TODO: Implement receipt download
  }
}
```

---

## Routes to Add

Update `app.routes.ts`:

```typescript
// Add to patient routes children:
{ path: 'upload-reports', component: UploadReportsComponent },
{ path: 'notifications', component: NotificationsComponent },
{ path: 'payment-history', component: PaymentHistoryComponent },
{ path: 'video-waiting-room/:appointmentId', component: VideoWaitingRoomComponent },

// Add to doctor routes children:
{ path: 'patients', component: PatientsListComponent },
{ path: 'video-queue', component: VideoQueueComponent },
{ path: 'analytics', component: ConsultationAnalyticsComponent },

// Add to admin routes children:
{ path: 'departments', component: DepartmentsComponent },
```

Update navigation components to add these routes to their menus.

---

## Summary of Work Remaining

### High Priority (Must Have):
1. ✅ Departments component - DONE
2. ⚠️ Patients-list component - TS done, needs HTML/SCSS
3. ❌ Video-queue component - Needs full implementation
4. ❌ Video-waiting-room component - Needs full implementation

### Medium Priority (Should Have):
5. ❌ Notifications component - Needs full implementation
6. ❌ Payment-history component - Needs full implementation
7. ❌ Consultation-analytics component - Needs full implementation

### Fixes Needed:
8. ❌ Prescriptions PDF download - Fix line 84
9. ❌ Video consultation room - Complete Twilio integration

### Routes:
10. ❌ Update app.routes.ts with all new routes
11. ❌ Update navigation menus (patient/doctor/admin dashboards)

---

*Use this guide to complete remaining implementations systematically.*
