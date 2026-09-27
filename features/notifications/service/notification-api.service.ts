import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


import { Notification } from '../models/notification.models';
import { environment } from '../../../../environments/environments';

@Injectable({
  providedIn: 'root'
})
export class NotificationApiService {

  private readonly http = inject(HttpClient);

  private readonly baseUrl =
    `${environment.apiUrl}/api/notifications`;

  getMine(): Observable<Notification[]> {
    return this.http.get<Notification[]>(this.baseUrl);
  }

  getUnread(): Observable<Notification[]> {
    return this.http.get<Notification[]>(
      `${this.baseUrl}/unread`
    );
  }

  markAsRead(id: number): Observable<Notification> {
    return this.http.patch<Notification>(
      `${this.baseUrl}/${id}/read`,
      {}
    );
  }
}

