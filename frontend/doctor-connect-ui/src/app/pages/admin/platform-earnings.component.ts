import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, DollarSign, TrendingUp, Users, CreditCard, Calendar, Filter } from 'lucide-angular';
import { PlatformEarningsService, DashboardStats, PlatformEarningsSummary, PaymentBreakdown } from '../../services/platform-earnings.service';

@Component({
  selector: 'app-platform-earnings',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  templateUrl: './platform-earnings.component.html',
  styleUrl: './platform-earnings.component.scss',
})
export class PlatformEarningsComponent implements OnInit {
  readonly DollarSign = DollarSign;
  readonly TrendingUp = TrendingUp;
  readonly Users = Users;
  readonly CreditCard = CreditCard;
  readonly Calendar = Calendar;
  readonly Filter = Filter;

  dashboardStats: DashboardStats | null = null;
  paymentBreakdown: PaymentBreakdown[] = [];
  allMethodsSummary: Record<string, PlatformEarningsSummary> | null = null;
  isLoading = false;
  isLoadingBreakdown = false;

  // Date filters
  startDate = '';
  endDate = '';
  showDateFilter = false;

  constructor(private earningsService: PlatformEarningsService) {
    // Set default date range to current month
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    this.startDate = this.formatDate(firstDay);
    this.endDate = this.formatDate(now);
  }

  ngOnInit(): void {
    this.loadDashboardStats();
    this.loadAllMethodsSummary();
    this.loadPaymentBreakdown();
  }

  loadDashboardStats(): void {
    this.isLoading = true;
    this.earningsService.getDashboardStats().subscribe({
      next: (stats) => {
        this.dashboardStats = stats;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load dashboard stats', error);
        this.isLoading = false;
      }
    });
  }

  loadAllMethodsSummary(): void {
    this.earningsService.getAllPaymentMethodsSummary().subscribe({
      next: (summary) => {
        this.allMethodsSummary = summary;
      },
      error: (error) => console.error('Failed to load all methods summary', error)
    });
  }

  loadPaymentBreakdown(): void {
    this.isLoadingBreakdown = true;
    this.earningsService.getStripePaymentBreakdown(this.startDate, this.endDate).subscribe({
      next: (breakdown) => {
        this.paymentBreakdown = breakdown;
        this.isLoadingBreakdown = false;
      },
      error: (error) => {
        console.error('Failed to load payment breakdown', error);
        this.isLoadingBreakdown = false;
      }
    });
  }

  applyDateFilter(): void {
    if (this.startDate && this.endDate) {
      this.loadPaymentBreakdown();
    }
  }

  clearDateFilter(): void {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    this.startDate = this.formatDate(firstDay);
    this.endDate = this.formatDate(now);
    this.loadPaymentBreakdown();
  }

  toggleDateFilter(): void {
    this.showDateFilter = !this.showDateFilter;
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  formatCurrency(amount: number | undefined): string {
    if (!amount) return '৳0.00';
    return `৳${amount.toFixed(2)}`;
  }

  formatDateDisplay(date: Date | undefined): string {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }
}
