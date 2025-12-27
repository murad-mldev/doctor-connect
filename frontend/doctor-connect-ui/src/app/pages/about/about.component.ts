import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import {
  LucideAngularModule,
  Users,
  Stethoscope,
  Video,
  Calendar,
  FileText,
  Shield,
  CheckCircle,
} from 'lucide-angular';

interface Feature {
  icon: any;
  title: string;
  description: string;
}

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule, LucideAngularModule],
  templateUrl: './about.component.html',
  styleUrl: './about.component.scss',
})
export class AboutComponent {
  readonly Users = Users;
  readonly Stethoscope = Stethoscope;
  readonly Video = Video;
  readonly Calendar = Calendar;
  readonly FileText = FileText;
  readonly Shield = Shield;
  readonly CheckCircle = CheckCircle;

  features: Feature[] = [
    {
      icon: Video,
      title: 'Video Consultations',
      description: 'Secure HD video calls with certified doctors from anywhere',
    },
    {
      icon: Calendar,
      title: 'Easy Scheduling',
      description: 'Book and manage appointments with real-time availability',
    },
    {
      icon: FileText,
      title: 'Digital Records',
      description:
        'Securely store and access prescriptions and medical history',
    },
    {
      icon: Stethoscope,
      title: 'Verified Doctors',
      description: 'All practitioners are verified and reviewed by patients',
    },
    {
      icon: Shield,
      title: 'Privacy & Security',
      description: 'Enterprise-grade security to keep your health data private',
    },
    {
      icon: Users,
      title: 'Patient-centric',
      description:
        'Designed to make healthcare more accessible, affordable and convenient',
    },
  ];

  constructor(private router: Router) {}

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
