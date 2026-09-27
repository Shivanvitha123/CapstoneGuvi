
import { createReducer, on } from '@ngrx/store';

import * as NotificationActions from './notification.actions';
import { Notification } from '../models/notification.models';

export interface NotificationState {
  notifications: Notification[];
  loading: boolean;
  error: string | null;
}

export const initialState: NotificationState = {
  notifications: [],
  loading: false,
  error: null
};

export const notificationReducer = createReducer(
  initialState,

  on(
    NotificationActions.loadNotifications,
    state => ({
      ...state,
      loading: true,
      error: null
    })
  ),

  on(
    NotificationActions.loadNotificationsSuccess,
    (state, { notifications }) => ({
      ...state,
      notifications,
      loading: false
    })
  ),

  on(
    NotificationActions.loadNotificationsFailure,
    (state, { error }) => ({
      ...state,
      loading: false,
      error
    })
  ),

  on(
    NotificationActions.markNotificationReadSuccess,
    (state, { notification }) => ({
      ...state,
      notifications: state.notifications.map(item =>
        item.id === notification.id
          ? notification
          : item
      )
    })
  )
);

