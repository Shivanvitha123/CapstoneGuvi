import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../../features/auth/models/auth.model';
import { environment } from '../../../environments/environments';


@Injectable({ providedIn: 'root' })
export class AuthApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.base}/api/auth/login`,
      request
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.base}/api/auth/register`,
      request
    );
  }

  me(): Observable<AuthResponse> {
    return this.http.get<AuthResponse>(
      `${this.base}/api/auth/me`
    );
  }
}

