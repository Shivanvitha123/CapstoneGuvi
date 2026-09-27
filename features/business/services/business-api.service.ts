import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environments';

import {
  Business,
  BusinessRequest,
  BusinessSummary
} from '../model/business.model';

export interface BusinessDeletionRequest {
  id: number;
  businessId: number;
  ownerId: number;
  reviewerId: number | null;
  reason: string;
  status: string;
  createdAt: string;
  reviewedAt: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class BusinessApiService {
  private readonly http = inject(HttpClient);

  private readonly baseUrl =
    `${environment.apiUrl}/api/business`;

  // -----------------------------------------
  // BUSINESS OWNER: CREATE BUSINESS
  // POST /api/business
  // -----------------------------------------

  create(request: BusinessRequest): Observable<Business> {
    return this.http.post<Business>(
      this.baseUrl,
      request
    );
  }

  // -----------------------------------------
  // BUSINESS OWNER: GET OWN BUSINESSES
  // GET /api/business/me
  // -----------------------------------------

  getMyBusinesses(): Observable<Business[]> {
    return this.http.get<Business[]>(
      `${this.baseUrl}/me`
    );
  }

  // -----------------------------------------
  // GET BUSINESS BY ID
  // GET /api/business/{id}
  // -----------------------------------------

  getById(id: number): Observable<Business> {
    return this.http.get<Business>(
      `${this.baseUrl}/${id}`
    );
  }

  // -----------------------------------------
  // BUSINESS OWNER: UPDATE BUSINESS
  // PUT /api/business/{id}
  // -----------------------------------------

  update(
    id: number,
    request: Partial<BusinessRequest>
  ): Observable<Business> {
    return this.http.put<Business>(
      `${this.baseUrl}/${id}`,
      request
    );
  }

  // -----------------------------------------
  // ADMIN / UNDERWRITER:
  // GET ALL BUSINESSES
  // GET /api/business
  // -----------------------------------------

  getAll(): Observable<BusinessSummary[]> {
    return this.http.get<BusinessSummary[]>(
      this.baseUrl
    );
  }

  // -----------------------------------------
  // BUSINESS OWNER: SUBMIT DELETION REQUEST
  // POST /api/business/{id}/deletion-requests
  // -----------------------------------------

  requestDeletion(
    businessId: number,
    reason: string
  ): Observable<BusinessDeletionRequest> {
    return this.http.post<BusinessDeletionRequest>(
      `${this.baseUrl}/${businessId}/deletion-requests`,
      {
        reason
      }
    );
  }

  // -----------------------------------------
  // BUSINESS OWNER: GET OWN DELETION REQUESTS
  // GET /api/business/deletion-requests/me
  // -----------------------------------------

  getMyDeletionRequests():
    Observable<BusinessDeletionRequest[]> {
    return this.http.get<BusinessDeletionRequest[]>(
      `${this.baseUrl}/deletion-requests/me`
    );
  }

  // -----------------------------------------
  // ADMIN / UNDERWRITER:
  // GET PENDING DELETION REQUESTS
  // GET /api/business/deletion-requests/pending
  // -----------------------------------------

  getPendingDeletionRequests():
    Observable<BusinessDeletionRequest[]> {
    return this.http.get<BusinessDeletionRequest[]>(
      `${this.baseUrl}/deletion-requests/pending`
    );
  }

  // -----------------------------------------
  // ADMIN / UNDERWRITER: APPROVE REQUEST
  // PUT /api/business/deletion-requests/{id}/approve
  // -----------------------------------------

  approveDeletionRequest(
    requestId: number
  ): Observable<BusinessDeletionRequest> {
    return this.http.put<BusinessDeletionRequest>(
      `${this.baseUrl}/deletion-requests/${requestId}/approve`,
      {}
    );
  }

  // -----------------------------------------
  // ADMIN / UNDERWRITER: REJECT REQUEST
  // PUT /api/business/deletion-requests/{id}/reject
  // -----------------------------------------

  rejectDeletionRequest(
    requestId: number
  ): Observable<BusinessDeletionRequest> {
    return this.http.put<BusinessDeletionRequest>(
      `${this.baseUrl}/deletion-requests/${requestId}/reject`,
      {}
    );
  }
}