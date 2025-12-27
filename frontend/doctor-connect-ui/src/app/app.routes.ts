import { Routes } from '@angular/router';

// Home Page
import { Home } from './home/home';
import { AboutComponent } from './pages/about/about.component';

// Public Pages
import { BrowseDoctorsComponent } from './pages/public/browse-doctors.component';
import { DoctorDetailsComponent } from './pages/public/doctor-details.component';

// Auth Pages
import { LoginComponent } from './pages/auth/login.component';
import { RegisterComponent } from './pages/auth/register.component';
import { AdminLoginComponent } from './pages/auth/admin-login.component';

// Patient Pages
import { PatientDashboardComponent } from './pages/patient/patient-dashboard.component';
import { DoctorSearchComponent } from './pages/patient/doctor-search.component';
import { MyAppointmentsComponent } from './pages/patient/my-appointments.component';
import { PrescriptionsComponent } from './pages/patient/prescriptions.component';
import { MedicalHistoryComponent } from './pages/patient/medical-history.component';
import { PatientProfileComponent } from './pages/patient/profile.component';
import { UploadReportsComponent } from './pages/patient/upload-reports.component';
import { NotificationsComponent } from './pages/patient/notifications.component';
import { PaymentHistoryComponent } from './pages/patient/payment-history.component';
import { VideoWaitingRoomComponent } from './pages/patient/video-waiting-room.component';

// Doctor Pages
import { DoctorDashboardComponent } from './pages/doctor/doctor-dashboard.component';
import { DoctorAppointmentsComponent } from './pages/doctor/appointments.component';
import { DoctorScheduleComponent } from './pages/doctor/schedule.component';
import { DoctorProfileComponent } from './pages/doctor/profile.component';
import { PatientsListComponent } from './pages/doctor/patients-list.component';
import { VideoQueueComponent } from './pages/doctor/video-queue.component';
import { ConsultationAnalyticsComponent } from './pages/doctor/consultation-analytics.component';

// Admin Pages
import { AdminDashboardComponent } from './pages/admin/admin-dashboard.component';
import { DoctorVerificationComponent } from './pages/admin/doctor-verification.component';
import { MedicinesComponent } from './pages/admin/medicines.component';
import { LabTestsComponent } from './pages/admin/lab-tests.component';
import { UsersComponent } from './pages/admin/users/users.component';
import { RolesComponent } from './pages/admin/roles.component';
import { DepartmentsComponent } from './pages/admin/departments.component';
import { PlatformEarningsComponent } from './pages/admin/platform-earnings.component';

// Video Pages
import { VideoConsultationRoomComponent } from './pages/video/video-consultation-room.component';

// Guards
import { authGuard, roleGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: Home,
  },

  // Public routes
  { path: 'browse-doctors', component: BrowseDoctorsComponent },
  { path: 'doctors/:id', component: DoctorDetailsComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'admin/login', component: AdminLoginComponent },
  { path: 'about', component: AboutComponent },

  // Patient routes (protected)
  {
    path: 'patient/dashboard',
    component: PatientDashboardComponent,
    canActivate: [roleGuard(['ROLE_USER', 'ROLE_PATIENT'])],
    children: [
      { path: 'search-doctors', component: DoctorSearchComponent },
      { path: 'appointments', component: MyAppointmentsComponent },
      { path: 'prescriptions', component: PrescriptionsComponent },
      { path: 'medical-history', component: MedicalHistoryComponent },
      { path: 'profile', component: PatientProfileComponent },
      { path: 'upload-reports', component: UploadReportsComponent },
      { path: 'notifications', component: NotificationsComponent },
      { path: 'payment-history', component: PaymentHistoryComponent },
    ],
  },

  // Patient video waiting room (standalone route)
  {
    path: 'video-waiting-room/:appointmentId',
    component: VideoWaitingRoomComponent,
    canActivate: [roleGuard(['ROLE_USER', 'ROLE_PATIENT'])],
  },

  // Doctor routes (protected)
  {
    path: 'doctor/dashboard',
    component: DoctorDashboardComponent,
    canActivate: [roleGuard(['ROLE_DOCTOR'])],
    children: [
      { path: 'appointments', component: DoctorAppointmentsComponent },
      { path: 'schedule', component: DoctorScheduleComponent },
      { path: 'profile', component: DoctorProfileComponent },
      { path: 'patients', component: PatientsListComponent },
      { path: 'video-queue', component: VideoQueueComponent },
      { path: 'analytics', component: ConsultationAnalyticsComponent },
    ],
  },

  // Admin routes (protected)
  {
    path: 'admin/dashboard',
    component: AdminDashboardComponent,
    canActivate: [roleGuard(['ROLE_ADMIN', 'ADMIN'])],
    children: [
      { path: 'doctor-verification', component: DoctorVerificationComponent },
      { path: 'medicines', component: MedicinesComponent },
      { path: 'lab-tests', component: LabTestsComponent },
      { path: 'users', component: UsersComponent },
      { path: 'roles', component: RolesComponent },
      { path: 'departments', component: DepartmentsComponent },
      { path: 'platform-earnings', component: PlatformEarningsComponent },
    ],
  },

  // Video consultation routes (protected)
  {
    path: 'video-consultation',
    canActivate: [authGuard],
    children: [
      { path: ':appointmentId', component: VideoConsultationRoomComponent },
    ],
  },

  // Wildcard route
  { path: '**', redirectTo: '' },
];
