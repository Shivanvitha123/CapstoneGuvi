import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  RiskAssessment,
  RiskAssessmentRequest,
  Simulation,
  SimulationRequest
} from '../models/risk.models';

import { environment } from '../../../../environments/environments';

@Injectable({
  providedIn: 'root'
})
export class RiskApiService {

  private readonly http = inject(HttpClient);

  private readonly assessmentUrl =
    `${environment.apiUrl}/api/risk/assessments`;

  private readonly simulationUrl =
    `${environment.apiUrl}/api/risk/simulations`;

  // Create a risk assessment
  // Backend should allow this only for Risk Engineers.
  createAssessment(
    request: RiskAssessmentRequest
  ): Observable<RiskAssessment> {

    return this.http.post<RiskAssessment>(
      this.assessmentUrl,
      request
    );
  }

  // Get all risk assessments
  // Backend should allow authorized users to view them.
  getAllAssessments(): Observable<RiskAssessment[]> {

    return this.http.get<RiskAssessment[]>(
      this.assessmentUrl
    );
  }

  // Get a specific risk assessment
  getAssessment(
    id: number
  ): Observable<RiskAssessment> {

    return this.http.get<RiskAssessment>(
      `${this.assessmentUrl}/${id}`
    );
  }

  // Get risk assessments for a specific business
  getByBusiness(
    businessId: number
  ): Observable<RiskAssessment[]> {

    return this.http.get<RiskAssessment[]>(
      `${this.assessmentUrl}/business/${businessId}`
    );
  }

  // Create a risk simulation
  createSimulation(
    request: SimulationRequest
  ): Observable<Simulation> {

    return this.http.post<Simulation>(
      this.simulationUrl,
      request
    );
  }

  // Get a specific risk simulation
  getSimulation(
    id: number
  ): Observable<Simulation> {

    return this.http.get<Simulation>(
      `${this.simulationUrl}/${id}`
    );
  }

  // Get all risk simulations
  getSimulations(): Observable<Simulation[]> {

    return this.http.get<Simulation[]>(
      this.simulationUrl
    );
  }
}