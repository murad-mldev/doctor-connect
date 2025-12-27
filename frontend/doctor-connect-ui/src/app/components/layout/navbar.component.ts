import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, Menu, X } from 'lucide-angular';
import { LanguageSwitcherComponent } from '../shared/language-switcher.component';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, LanguageSwitcherComponent],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent {
  isMenuOpen = false;

  // Lucide icons
  readonly Menu = Menu;
  readonly X = X;

  constructor(private router: Router) {}

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }

  navigateToRegister() {
    this.router.navigate(['/register']);
  }

  navigateToAbout() {
    this.router.navigate(['/about']);
  }

  navigateToDoctorSearch() {
    this.router.navigate(['/patient/search-doctors']);
  }
}
