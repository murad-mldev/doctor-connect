import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { UserService, NotificationService } from '../../services';
import { UserProfileResponse } from '../../models';
import { LanguageSwitcherComponent } from './language-switcher.component';

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule, LanguageSwitcherComponent],
  template: `
    <nav class="bg-white shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex">
            <div class="flex-shrink-0 flex items-center">
              <a routerLink="/" class="text-2xl font-bold text-blue-600">{{ 'common.appName' | translate }}</a>
            </div>
            <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
              <a *ngIf="userRole === 'PATIENT'"
                 routerLink="/patient/dashboard"
                 routerLinkActive="border-blue-500 text-gray-900"
                 class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                {{ 'navigation.dashboard' | translate }}
              </a>
              <a *ngIf="userRole === 'PATIENT'"
                 routerLink="/patient/search-doctors"
                 routerLinkActive="border-blue-500 text-gray-900"
                 class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                {{ 'doctor.findDoctor' | translate }}
              </a>
              <a *ngIf="userRole === 'PATIENT'"
                 routerLink="/patient/appointments"
                 routerLinkActive="border-blue-500 text-gray-900"
                 class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                {{ 'appointment.myAppointments' | translate }}
              </a>

              <a *ngIf="userRole === 'DOCTOR'"
                 routerLink="/doctor/dashboard"
                 routerLinkActive="border-blue-500 text-gray-900"
                 class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                {{ 'navigation.dashboard' | translate }}
              </a>
              <a *ngIf="userRole === 'DOCTOR'"
                 routerLink="/doctor/appointments"
                 routerLinkActive="border-blue-500 text-gray-900"
                 class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                {{ 'navigation.appointments' | translate }}
              </a>
              <a *ngIf="userRole === 'DOCTOR'"
                 routerLink="/doctor/schedule"
                 routerLinkActive="border-blue-500 text-gray-900"
                 class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                {{ 'doctor.schedule' | translate }}
              </a>

              <a *ngIf="userRole === 'ADMIN'"
                 routerLink="/admin/dashboard"
                 routerLinkActive="border-blue-500 text-gray-900"
                 class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                {{ 'navigation.dashboard' | translate }}
              </a>
              <a *ngIf="userRole === 'ADMIN'"
                 routerLink="/admin/doctor-verification"
                 routerLinkActive="border-blue-500 text-gray-900"
                 class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                {{ 'navigation.doctors' | translate }}
              </a>
            </div>
          </div>

          <div class="flex items-center">
            <div class="mr-4">
              <app-language-switcher></app-language-switcher>
            </div>
            <div *ngIf="isLoggedIn" class="flex items-center space-x-4">
              <button class="relative p-1 rounded-full text-gray-400 hover:text-gray-500">
                <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
                </svg>
                <span *ngIf="unreadNotifications > 0" class="absolute top-0 right-0 block h-2 w-2 rounded-full bg-red-400"></span>
              </button>

              <div class="relative">
                <button
                  (click)="toggleDropdown()"
                  class="flex items-center space-x-2 text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                  <div class="h-8 w-8 rounded-full bg-blue-500 flex items-center justify-center text-white font-medium">
                    {{ userProfile?.fullName?.charAt(0) || 'U' }}
                  </div>
                  <span class="hidden md:block text-gray-700">{{ userProfile?.fullName }}</span>
                  <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                  </svg>
                </button>

                <div *ngIf="showDropdown" class="origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5">
                  <a routerLink="/profile" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">{{ 'profile.myProfile' | translate }}</a>
                  <a routerLink="/settings" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">{{ 'navigation.settings' | translate }}</a>
                  <button
                    (click)="logout()"
                    class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                    {{ 'common.signOut' | translate }}
                  </button>
                </div>
              </div>
            </div>

            <div *ngIf="!isLoggedIn" class="flex items-center space-x-4">
              <a routerLink="/login" class="text-gray-700 hover:text-gray-900">{{ 'common.signIn' | translate }}</a>
              <a routerLink="/register" class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">{{ 'common.signUp' | translate }}</a>
            </div>
          </div>
        </div>
      </div>
    </nav>
  `,
  styles: []
})
export class NavigationComponent implements OnInit {
  userProfile: UserProfileResponse | null = null;
  isLoggedIn = false;
  userRole = '';
  unreadNotifications = 0;
  showDropdown = false;

  constructor(
    private userService: UserService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.checkLoginStatus();
    if (this.isLoggedIn) {
      this.loadUserProfile();
      this.loadNotifications();
    }
  }

  checkLoginStatus(): void {
    const token = localStorage.getItem('token');
    this.isLoggedIn = !!token;
    this.userRole = localStorage.getItem('userRole') || '';
  }

  loadUserProfile(): void {
    this.userService.getCurrentUser().subscribe({
      next: (profile) => {
        this.userProfile = profile;
      },
      error: (error) => console.error('Failed to load profile', error)
    });
  }

  loadNotifications(): void {
    if (!this.userProfile?.id) return;

    this.notificationService.getUnreadCount(this.userProfile.id).subscribe({
      next: (response) => {
        this.unreadNotifications = response.count;
      },
      error: (error) => console.error('Failed to load notifications', error)
    });
  }

  toggleDropdown(): void {
    this.showDropdown = !this.showDropdown;
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    this.isLoggedIn = false;
    this.router.navigate(['/login']);
  }
}
