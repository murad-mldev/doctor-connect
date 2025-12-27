import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DoctorProfile } from '../../models';

@Component({
  selector: 'app-doctor-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './doctor-card.component.html',
  styleUrl: './doctor-card.component.scss',
})
export class DoctorCardComponent {
  @Input() doctor!: DoctorProfile;
}
