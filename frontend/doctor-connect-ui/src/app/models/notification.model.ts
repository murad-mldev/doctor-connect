import { NotificationType } from './notification-type.enum';

export interface Notification {
  id?: string;
  userId: string;
  type: NotificationType;
  title: string;
  body: string;
  metadata?: string;
  isRead?: boolean;
  createdAt?: string;
}

export interface CreateNotificationRequest {
  userId: string;
  type: NotificationType;
  title: string;
  body: string;
  metadata?: string;
}
