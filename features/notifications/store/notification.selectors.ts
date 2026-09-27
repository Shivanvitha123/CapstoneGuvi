
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { NotificationState } from './notification.reducer';

export const selectNotificationState =
  createFeatureSelector<NotificationState>('notifications');

export const selectNotifications = createSelector(
  selectNotificationState,
  state => state.notifications
);

export const selectUnreadNotifications = createSelector(
  selectNotifications,
  notifications =>
    notifications.filter(notification => !notification.read)
);

export const selectUnreadCount = createSelector(
  selectUnreadNotifications,
  notifications => notifications.length
);

export const selectNotificationsLoading = createSelector(
  selectNotificationState,
  state => state.loading
);

export const selectNotificationsError = createSelector(
  selectNotificationState,
  state => state.error
);

