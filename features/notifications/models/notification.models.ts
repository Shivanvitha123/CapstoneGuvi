export type NotificationType =
  | 'INFO'
  | 'SUCCESS'
  | 'WARNING'
  | 'ACTION_REQUIRED';

export interface Notification {
  id: number;
  recipientUserId: number;
  type: NotificationType;
  title: string;
  message: string;
  referenceType: string | null;
  referenceId: number | null;
  read: boolean;
  createdAt: string;
}
