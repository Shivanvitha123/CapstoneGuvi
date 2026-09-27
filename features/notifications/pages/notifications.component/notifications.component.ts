
import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';

import * as NotificationActions
  from '../../store/notification.actions';

import {
  selectNotifications,
  selectNotificationsLoading,
  selectNotificationsError
} from '../../store/notification.selectors';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NotificationsComponent implements OnInit {

  private readonly store = inject(Store);

  readonly notifications$ =
    this.store.select(selectNotifications);

  readonly loading$ =
    this.store.select(selectNotificationsLoading);

  readonly error$ =
    this.store.select(selectNotificationsError);

  ngOnInit(): void {
    this.store.dispatch(
      NotificationActions.loadNotifications()
    );
  }

  markAsRead(id: number): void {
    this.store.dispatch(
      NotificationActions.markNotificationRead({ id })
    );
  }

  trackById(
    _index: number,
    notification: { id: number }
  ): number {
    return notification.id;
  }
}
