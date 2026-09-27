import { Injectable, OnDestroy, signal } from '@angular/core';

export interface Notification {
  type: 'success' | 'error' | 'info';
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService implements OnDestroy {
  readonly current = signal<Notification | null>(null);

  private notificationTimer?: ReturnType<typeof setTimeout>;
  private showTimer?: ReturnType<typeof setTimeout>;

  show(
    message: string,
    type: Notification['type'] = 'info'
  ): void {
    this.clearTimers();

    this.showTimer = setTimeout(() => {
      this.current.set({ message, type });
      this.showTimer = undefined;

      this.notificationTimer = setTimeout(() => {
        this.current.set(null);
        this.notificationTimer = undefined;
      }, 3500);
    }, 0);
  }

  success(message: string): void {
    this.show(message, 'success');
  }

  error(message: string): void {
    this.show(message, 'error');
  }

  info(message: string): void {
    this.show(message, 'info');
  }

  clear(): void {
    this.clearTimers();
    this.current.set(null);
  }

  private clearTimers(): void {
    if (this.notificationTimer) {
      clearTimeout(this.notificationTimer);
      this.notificationTimer = undefined;
    }

    if (this.showTimer) {
      clearTimeout(this.showTimer);
      this.showTimer = undefined;
    }
  }

  ngOnDestroy(): void {
    this.clearTimers();
  }
}