import { createAction, props } from '@ngrx/store';

import {
  Business,
  BusinessRequest,
  BusinessSummary
} from '../model/business.model';

export const loadBusinesses =
  createAction('[Business] Load Businesses');

export const loadBusinessesSuccess = createAction(
  '[Business] Load Businesses Success',
  props<{ businesses: Business[] }>()
);

export const loadBusinessesFailure = createAction(
  '[Business] Load Businesses Failure',
  props<{ error: string }>()
);

export const createBusiness = createAction(
  '[Business] Create Business',
  props<{ request: BusinessRequest }>()
);

export const createBusinessSuccess = createAction(
  '[Business] Create Business Success',
  props<{ business: Business }>()
);

export const createBusinessFailure = createAction(
  '[Business] Create Business Failure',
  props<{ error: string }>()
);

export const updateBusiness = createAction(
  '[Business] Update Business',
  props<{
    id: number;
    request: Partial<BusinessRequest>;
  }>()
);

export const updateBusinessSuccess = createAction(
  '[Business] Update Business Success',
  props<{ business: Business }>()
);

export const updateBusinessFailure = createAction(
  '[Business] Update Business Failure',
  props<{ error: string }>()
);

export const loadAllBusinesses =
  createAction('[Business] Load All Businesses');

export const loadAllBusinessesSuccess = createAction(
  '[Business] Load All Businesses Success',
  props<{ businesses: BusinessSummary[] }>()
);

export const loadAllBusinessesFailure = createAction(
  '[Business] Load All Businesses Failure',
  props<{ error: string }>()
);

