import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule, LogOut } from 'lucide-angular';

export interface MenuItem {
  icon: any; // Lucide icon
  label: string;
  route: string;
  badge?: number;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss',
})
export class SidebarComponent {
  @Input() menuItems: MenuItem[] = [];
  @Input() userRole: string = '';
  @Output() logoutClicked = new EventEmitter<void>();

  LogOut = LogOut;
  isCollapsed = false;

  toggleSidebar(): void {
    this.isCollapsed = !this.isCollapsed;
  }

  onLogout(): void {
    this.logoutClicked.emit();
  }
}
