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
