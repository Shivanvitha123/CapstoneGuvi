import { createReducer, on } from '@ngrx/store';

import * as BusinessActions from './business.actions';

import {
  Business,
  BusinessSummary
} from '../model/business.model';

export interface BusinessState {
  businesses: Business[];
  summaries: BusinessSummary[];
  loading: boolean;
  saving: boolean;
  error: string | null;
}

export const initialState: BusinessState = {
  businesses: [],
  summaries: [],
  loading: false,
  saving: false,
  error: null
};

export const businessReducer = createReducer(
  initialState,

  // ---------------- LOAD OWNER BUSINESSES ----------------

  on(
    BusinessActions.loadBusinesses,
    state => ({
      ...state,
      loading: true,
      error: null
    })
  ),

  on(
    BusinessActions.loadBusinessesSuccess,
    (state, { businesses }) => ({
      ...state,
      businesses,
      loading: false,
      error: null
    })
  ),

  on(
    BusinessActions.loadBusinessesFailure,
    (state, { error }) => ({
      ...state,
      loading: false,
      error
    })
  ),

  // ---------------- CREATE / UPDATE ----------------

  on(
    BusinessActions.createBusiness,
    BusinessActions.updateBusiness,
    state => ({
      ...state,
      saving: true,
      error: null
    })
  ),

  on(
    BusinessActions.createBusinessSuccess,
    (state, { business }) => ({
      ...state,
      businesses: [
        ...state.businesses,
        business
      ],
      saving: false,
      error: null
    })
  ),

  on(
    BusinessActions.updateBusinessSuccess,
    (state, { business }) => ({
      ...state,
      businesses: state.businesses.map(item =>
        item.id === business.id ? business : item
      ),
      saving: false,
      error: null
    })
  ),

  on(
    BusinessActions.createBusinessFailure,
    BusinessActions.updateBusinessFailure,
    (state, { error }) => ({
      ...state,
      saving: false,
      error
    })
  ),

  // ---------------- LOAD ALL BUSINESSES ----------------

  on(
    BusinessActions.loadAllBusinesses,
    state => ({
      ...state,
      loading: true,
      error: null
    })
  ),

  on(
    BusinessActions.loadAllBusinessesSuccess,
    (state, { businesses }) => ({
      ...state,
      summaries: businesses,
      loading: false,
      error: null
    })
  ),

  on(
    BusinessActions.loadAllBusinessesFailure,
    (state, { error }) => ({
      ...state,
      loading: false,
      error
    })
  )
);