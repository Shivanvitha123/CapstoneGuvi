import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';



import {
  CreatePolicyRequest,
  Policy,
  UpdatePolicyRequest
} from '../models/policy.models';
import { environment } from '../../../../environments/environments';

@Injectable({
  providedIn: 'root'
})
export class PolicyApiService {

  private readonly http = inject(HttpClient);

  private readonly baseUrl =
    `${environment.apiUrl}/api/policies`;

  createPolicy(
    request: CreatePolicyRequest
  ): Observable<Policy> {

    return this.http.post<Policy>(
      this.baseUrl,
      request
    );
  }

  getPolicy(
    policyId: number
  ): Observable<Policy> {

    return this.http.get<Policy>(
      `${this.baseUrl}/${policyId}`
    );
  }

  getOwnerPolicies(): Observable<Policy[]> {

    return this.http.get<Policy[]>(
      `${this.baseUrl}/owner`
    );
  }

  getAllPolicies(): Observable<Policy[]> {

    return this.http.get<Policy[]>(
      this.baseUrl
    );
  }

  updatePolicy(
    policyId: number,
    request: UpdatePolicyRequest
  ): Observable<Policy> {

    return this.http.put<Policy>(
      `${this.baseUrl}/${policyId}`,
      request
    );
  }

  submitPolicy(
    policyId: number
  ): Observable<Policy> {

    return this.http.post<Policy>(
      `${this.baseUrl}/${policyId}/submit`,
      {}
    );
  }

  updatePolicyStatus(
    policyId: number,
    status: string
  ): Observable<Policy> {

    return this.http.patch<Policy>(
      `${this.baseUrl}/${policyId}/status`,
      {},
      {
        params: {
          status
        }
      }
    );
  }
}
