import { createReducer, on } from '@ngrx/store';

import {
  RiskAssessment,
  Simulation
} from '../models/risk.models';

import * as RiskActions from './risk.actions';

export interface RiskState {

  assessments: RiskAssessment[];

  simulations: Simulation[];

  loading: boolean;

  error: string | null;

  successMessage: string | null;
}

export const initialRiskState: RiskState = {

  assessments: [],

  simulations: [],

  loading: false,

  error: null,

  successMessage: null
};

export const riskReducer = createReducer(

  initialRiskState,

  on(
    RiskActions.loadAssessments,
    RiskActions.createAssessment,
    RiskActions.loadSimulations,
    RiskActions.createSimulation,

    state => ({
      ...state,
      loading: true,
      error: null
    })
  ),

  on(
    RiskActions.loadAssessmentsSuccess,

    (state, { assessments }) => ({
      ...state,
      loading: false,
      assessments
    })
  ),

  on(
    RiskActions.createAssessmentSuccess,

    (state, { assessment }) => ({
      ...state,
      loading: false,
      assessments: [
        assessment,
        ...state.assessments
      ],
      successMessage:
        'Risk assessment created successfully.'
    })
  ),

  on(
    RiskActions.loadSimulationsSuccess,

    (state, { simulations }) => ({
      ...state,
      loading: false,
      simulations
    })
  ),

  on(
    RiskActions.createSimulationSuccess,

    (state, { simulation }) => ({
      ...state,
      loading: false,
      simulations: [
        simulation,
        ...state.simulations
      ],
      successMessage:
        'Risk simulation created successfully.'
    })
  ),

  on(
    RiskActions.riskFailure,

    (state, { error }) => ({
      ...state,
      loading: false,
      error
    })
  ),

  on(
    RiskActions.clearRiskMessages,

    state => ({
      ...state,
      error: null,
      successMessage: null
    })
  )
);

