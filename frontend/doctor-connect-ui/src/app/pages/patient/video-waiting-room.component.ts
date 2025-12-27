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
