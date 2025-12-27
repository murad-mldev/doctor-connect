export interface VideoRoom {
  id?: string;
  appointmentId?: string;
  meetUrl?: string;
  meetingCode?: string;
  conferenceId?: string;
  isActive?: boolean;
  expiresAt?: string;
  createdAt?: string;
}

export interface CreateVideoRoomRequest {
  appointmentId: string;
  participantName?: string;
}

export interface GenerateJoinTokenRequest {
  participantName: string;
}

export interface GenerateJoinTokenResponse {
  token: string;
  roomId: string;
}
