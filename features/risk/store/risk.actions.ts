import { createAction, props } from '@ngrx/store';

import {
  RiskAssessment,
  RiskAssessmentRequest,
  Simulation,
  SimulationRequest
} from '../models/risk.models';

// Load assessments for a specific business
export const loadAssessments = createAction(
  '[Risk] Load Assessments',
  props<{ businessId: number }>()
);

// Load all assessments for authorized viewers
export const loadAllAssessments = createAction(
  '[Risk] Load All Assessments'
);

export const loadAssessmentsSuccess = createAction(
  '[Risk] Load Assessments Success',
  props<{ assessments: RiskAssessment[] }>()
);

// Create assessment (backend restricts this to Risk Engineers)
export const createAssessment = createAction(
  '[Risk] Create Assessment',
  props<{ request: RiskAssessmentRequest }>()
);

export const createAssessmentSuccess = createAction(
  '[Risk] Create Assessment Success',
  props<{ assessment: RiskAssessment }>()
);

// Load simulations
export const loadSimulations = createAction(
  '[Risk] Load Simulations'
);

export const loadSimulationsSuccess = createAction(
  '[Risk] Load Simulations Success',
  props<{ simulations: Simulation[] }>()
);

// Create simulation
export const createSimulation = createAction(
  '[Risk] Create Simulation',
  props<{ request: SimulationRequest }>()
);

export const createSimulationSuccess = createAction(
  '[Risk] Create Simulation Success',
  props<{ simulation: Simulation }>()
);

// General failure
export const riskFailure = createAction(
  '[Risk] Failure',
  props<{ error: string }>()
);

// Clear messages
export const clearRiskMessages = createAction(
  '[Risk] Clear Messages'
);