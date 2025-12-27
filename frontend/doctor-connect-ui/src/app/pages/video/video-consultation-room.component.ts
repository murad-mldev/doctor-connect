import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { VideoConsultationService } from '../../services';
import { VideoRoom } from '../../models';

@Component({
  selector: 'app-video-consultation-room',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './video-consultation-room.component.html',
  styleUrl: './video-consultation-room.component.scss',
})
export class VideoConsultationRoomComponent implements OnInit, OnDestroy {
  videoRoom: VideoRoom | null = null;
  loading = true;
  errorMessage = '';
  appointmentId = '';
  isMicOn = true;
  isCameraOn = true;
  isScreenSharing = false;
  sanitizedMeetUrl: any;

  constructor(
    private route: ActivatedRoute,
    private videoService: VideoConsultationService
  ) {}

  ngOnInit(): void {
    this.appointmentId = this.route.snapshot.params['appointmentId'];
    this.loadVideoRoom();
  }

  ngOnDestroy(): void {
    // Cleanup code here
  }

  loadVideoRoom(): void {
    this.videoService.getVideoRoomByAppointment(this.appointmentId).subscribe({
      next: (room) => {
        this.videoRoom = room;
        this.sanitizedMeetUrl = room.meetUrl;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load video room. Please try again.';
        this.loading = false;
      }
    });
  }

  copyMeetingLink(): void {
    if (this.videoRoom?.meetUrl) {
      navigator.clipboard.writeText(this.videoRoom.meetUrl);
      alert('Meeting link copied to clipboard!');
    }
  }

  toggleMicrophone(): void {
    this.isMicOn = !this.isMicOn;
  }

  toggleCamera(): void {
    this.isCameraOn = !this.isCameraOn;
  }

  toggleScreenShare(): void {
    this.isScreenSharing = !this.isScreenSharing;
  }

  endConsultation(): void {
    if (confirm('Are you sure you want to end this consultation?')) {
      if (this.videoRoom?.id) {
        this.videoService.endVideoRoom(this.videoRoom.id).subscribe({
          next: () => {
            this.goBack();
          },
          error: (error) => console.error('Failed to end consultation', error)
        });
      }
    }
  }

  goBack(): void {
    window.history.back();
  }
}
