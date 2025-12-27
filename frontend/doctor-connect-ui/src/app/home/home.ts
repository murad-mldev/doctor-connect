import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import {
  LucideAngularModule,
  Video,
  Calendar,
  FileText,
  UserCheck,
  Shield,
  Headphones,
  UserPlus,
  Search,
  CalendarCheck,
  Users,
  Stethoscope,
  Activity,
  Star,
  ArrowRight,
  CheckCircle,
} from 'lucide-angular';
import { NavbarComponent } from '../components/layout/navbar.component';
import { FooterComponent } from '../components/layout/footer.component';

interface Feature {
  icon: any;
  title: string;
  description: string;
}

interface Step {
  number: number;
  title: string;
  description: string;
  icon: any;
}

interface Stat {
  value: string;
  label: string;
  icon: any;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule, LucideAngularModule],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  // Lucide icons
  readonly Video = Video;
  readonly Calendar = Calendar;
  readonly FileText = FileText;
  readonly UserCheck = UserCheck;
  readonly Shield = Shield;
  readonly Headphones = Headphones;
  readonly UserPlus = UserPlus;
  readonly Search = Search;
  readonly CalendarCheck = CalendarCheck;
  readonly Users = Users;
  readonly Stethoscope = Stethoscope;
  readonly Activity = Activity;
  readonly Star = Star;
  readonly ArrowRight = ArrowRight;
  readonly CheckCircle = CheckCircle;

  // Features data
  features: Feature[] = [
    {
      icon: Video,
      title: 'Video Consultations',
      description:
        'Connect with doctors through secure HD video calls from anywhere, anytime',
    },
    {
      icon: Calendar,
      title: 'Easy Scheduling',
      description:
        'Book appointments in seconds with real-time availability updates',
    },
    {
      icon: FileText,
      title: 'Digital Records',
      description:
        'Access prescriptions and medical history securely in one place',
    },
    {
      icon: UserCheck,
      title: 'Verified Doctors',
      description:
        'All healthcare professionals are thoroughly verified and certified',
    },
    {
      icon: Shield,
      title: 'Data Security',
      description:
        'Enterprise-grade encryption keeps your health information private',
    },
    {
      icon: Headphones,
      title: '24/7 Support',
      description: 'Our dedicated support team is always here to help you',
    },
  ];

  // How it works steps
  steps: Step[] = [
    {
      number: 1,
      title: 'Create Account',
      description: 'Quick and easy registration in under 2 minutes',
      icon: UserPlus,
    },
    {
      number: 2,
      title: 'Find Your Doctor',
      description: 'Browse specialists by department and availability',
      icon: Search,
    },
    {
      number: 3,
      title: 'Book & Consult',
      description: 'Schedule appointments or start instant consultations',
      icon: CalendarCheck,
    },
  ];

  // Statistics
  stats: Stat[] = [
    {
      value: '10,000+',
      label: 'Happy Patients',
      icon: Users,
    },
    {
      value: '500+',
      label: 'Verified Doctors',
      icon: Stethoscope,
    },
    {
      value: '50,000+',
      label: 'Consultations',
      icon: Activity,
    },
    {
      value: '4.9/5',
      label: 'Patient Rating',
      icon: Star,
    },
  ];

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Check if user is already logged in
    const token = localStorage.getItem('token');
    if (token) {
      const userRole = localStorage.getItem('userRole');
      this.redirectToDashboard(userRole || '');
    }
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  navigateToDoctorSearch(): void {
    const token = localStorage.getItem('token');
    if (token) {
      this.router.navigate(['/patient/search-doctors']);
    } else {
      this.router.navigate(['/register']);
    }
  }

  scrollToSection(sectionId: string): void {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  private redirectToDashboard(role: string): void {
    if (role === 'DOCTOR') {
      this.router.navigate(['/doctor/dashboard']);
    } else if (role === 'ADMIN') {
      this.router.navigate(['/admin/dashboard']);
    } else {
      this.router.navigate(['/patient/dashboard']);
    }
  }
}
