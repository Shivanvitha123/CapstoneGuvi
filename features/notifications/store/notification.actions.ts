
import { createAction, props } from '@ngrx/store';
import { Notification } from '../models/notification.models';

export const loadNotifications = createAction(
  '[Notifications] Load Notifications'
);

export const loadNotificationsSuccess = createAction(
  '[Notifications] Load Notifications Success',
  props<{ notifications: Notification[] }>()
);

export const loadNotificationsFailure = createAction(
  '[Notifications] Load Notifications Failure',
  props<{ error: string }>()
);

export const markNotificationRead = createAction(
  '[Notifications] Mark Notification Read',
  props<{ id: number }>()
);

export const markNotificationReadSuccess = createAction(
  '[Notifications] Mark Notification Read Success',
  props<{ notification: Notification }>()
);

export const markNotificationReadFailure = createAction(
  '[Notifications] Mark Notification Read Failure',
  props<{ error: string }>()
);
