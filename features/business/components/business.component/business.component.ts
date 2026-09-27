import { Component, inject } from '@angular/core';
import {
  AsyncPipe,
  CurrencyPipe,
  DatePipe
} from '@angular/common';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Store } from '@ngrx/store';
import {
  finalize,
  map,
  Observable
} from 'rxjs';

import * as BusinessActions
  from '../../store/business.actions';

import {
  selectBusinesses,
  selectSummaries,
  selectBusinessError,
  selectLoading,
  selectSaving
} from '../../store/business.selector';

import { PageHeaderComponent }
  from '../../../../shared/components/page-header.component/page-header.component';

import {
  BusinessApiService,
  BusinessDeletionRequest
} from '../../services/business-api.service';

import { TokenService }
  from '../../../../core/services/token.service';

@Component({
  selector: 'app-business',
  standalone: true,
  imports: [
    AsyncPipe,
    CurrencyPipe,
    DatePipe,
    ReactiveFormsModule,
    PageHeaderComponent
  ],
  templateUrl: './business.component.html',
  styleUrl: './business.component.css'
})
export class BusinessComponent {
  private readonly store = inject(Store);
  private readonly fb = inject(FormBuilder);
  private readonly businessApi = inject(BusinessApiService);
  private readonly tokenService = inject(TokenService);

  readonly businesses$ = this.store.select(selectBusinesses);
  readonly summaries$ = this.store.select(selectSummaries);
  readonly loading$ = this.store.select(selectLoading);
  readonly saving$ = this.store.select(selectSaving);
  readonly error$ = this.store.select(selectBusinessError);

  readonly role = String(
    this.tokenService.payload()?.['role'] ?? ''
  )
    .replace(/^ROLE_/i, '')
    .toUpperCase();

  // Roles that can view all businesses.
  readonly canViewAllBusinesses =
    this.role === 'ADMIN' ||
    this.role === 'UNDERWRITER' ||
    this.role === 'RISK_ENGINEER';

  // Only admins and underwriters review deletion requests.
  readonly isReviewer =
    this.role === 'ADMIN' ||
    this.role === 'UNDERWRITER';

  // Only business owners can create, edit, or request deletion.
  readonly canManageBusiness =
    this.role === 'BUSINESS_OWNER';

  editingId: number | null = null;
  showForm = false;

  showDeletionForm = false;
  selectedBusinessId: number | null = null;

  deletionRequests: BusinessDeletionRequest[] = [];

  deletionLoading = false;
  deletionSubmitting = false;

  deletionError = '';
  deletionSuccess = '';

  canReviewDeletionRequests = false;

  reviewingRequestIds = new Set<number>();

  readonly form = this.fb.nonNullable.group({
    businessName: ['', Validators.required],
    registrationNumber: ['', Validators.required],
    businessType: ['', Validators.required],
    industry: ['', Validators.required],
    address: ['', Validators.required],
    city: ['', Validators.required],
    state: ['', Validators.required],
    postalCode: ['', Validators.required],
    country: ['India', Validators.required],
    contactEmail: ['', [
      Validators.required,
      Validators.email
    ]],
    contactPhone: ['', Validators.required],
    annualRevenue: [0, [
      Validators.required,
      Validators.min(0)
    ]],
    employeeCount: [1, [
      Validators.required,
      Validators.min(1)
    ]],
    establishedDate: ['', Validators.required]
  });

  readonly deletionForm = this.fb.nonNullable.group({
    reason: ['', [
      Validators.required,
      Validators.maxLength(1000)
    ]]
  });

  constructor() {
    this.load();

    // Only owners and reviewers have deletion-request access.
    if (this.canManageBusiness || this.isReviewer) {
      this.loadDeletionRequests();
    }
  }

  // ---------------- BUSINESS ----------------

  load(): void {
    if (this.canViewAllBusinesses) {
      // Admin, Underwriter, and Risk Engineer.
      this.store.dispatch(
        BusinessActions.loadAllBusinesses()
      );
    } else if (this.canManageBusiness) {
      // Business Owner: own businesses only.
      this.store.dispatch(
        BusinessActions.loadBusinesses()
      );
    }
  }

  getBusiness(id: number) {
    let result: any;

    this.store.select(selectBusinesses)
      .subscribe(items => {
        result = items.find(item => item.id === id);
      })
      .unsubscribe();

    return result;
  }

  openCreate(): void {
    if (!this.canManageBusiness) {
      return;
    }

    this.editingId = null;

    this.form.reset({
      businessName: '',
      registrationNumber: '',
      businessType: '',
      industry: '',
      address: '',
      city: '',
      state: '',
      postalCode: '',
      country: 'India',
      contactEmail: '',
      contactPhone: '',
      annualRevenue: 0,
      employeeCount: 1,
      establishedDate: ''
    });

    this.showForm = true;
  }

  edit(id: number): void {
    if (!this.canManageBusiness) {
      return;
    }

    const business = this.getBusiness(id);

    if (!business) {
      return;
    }

    this.editingId = id;

    this.form.patchValue({
      businessName: business.businessName,
      registrationNumber: business.registrationNumber,
      businessType: business.businessType,
      industry: business.industry,
      address: business.address,
      city: business.city,
      state: business.state,
      postalCode: business.postalCode,
      country: business.country,
      contactEmail: business.contactEmail,
      contactPhone: business.contactPhone,
      annualRevenue: business.annualRevenue,
      employeeCount: business.employeeCount,
      establishedDate: business.establishedDate
    });

    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editingId = null;
  }

  save(): void {
    if (!this.canManageBusiness || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const request = this.form.getRawValue();

    if (this.editingId !== null) {
      this.store.dispatch(
        BusinessActions.updateBusiness({
          id: this.editingId,
          request
        })
      );
    } else {
      this.store.dispatch(
        BusinessActions.createBusiness({
          request
        })
      );
    }

    this.showForm = false;
  }

  // ---------------- DELETION REQUESTS ----------------

  loadDeletionRequests(): void {
    if (!this.canManageBusiness && !this.isReviewer) {
      this.deletionRequests = [];
      this.canReviewDeletionRequests = false;
      return;
    }

    this.deletionLoading = true;
    this.deletionError = '';

    this.getDeletionRequests().pipe(
      finalize(() => {
        this.deletionLoading = false;
      })
    ).subscribe({
      next: result => {
        this.deletionRequests = result.requests;
        this.canReviewDeletionRequests = result.reviewer;
      },
      error: (error: HttpErrorResponse) => {
        this.deletionRequests = [];
        this.canReviewDeletionRequests = false;

        this.deletionError =
          error?.error?.message ||
          'Could not load deletion requests. Please try again.';
      }
    });
  }

  private getDeletionRequests(): Observable<{
    requests: BusinessDeletionRequest[];
    reviewer: boolean;
  }> {
    if (this.isReviewer) {
      return this.businessApi.getPendingDeletionRequests().pipe(
        map(requests => ({
          requests,
          reviewer: true
        }))
      );
    }

    return this.businessApi.getMyDeletionRequests().pipe(
      map(requests => ({
        requests,
        reviewer: false
      }))
    );
  }

  hasPendingRequest(businessId: number): boolean {
    return this.deletionRequests.some(
      request =>
        request.businessId === businessId &&
        request.status?.toUpperCase() === 'PENDING'
    );
  }

  // ---------------- SUBMIT DELETION REQUEST ----------------

  openDeletionForm(businessId: number): void {
    if (!this.canManageBusiness) {
      return;
    }

    this.selectedBusinessId = businessId;

    this.deletionForm.reset({ reason: '' });
    this.deletionError = '';
    this.deletionSuccess = '';

    this.showDeletionForm = true;
  }

  closeDeletionForm(): void {
    this.showDeletionForm = false;
    this.selectedBusinessId = null;
    this.deletionForm.reset({ reason: '' });
  }

  submitDeletionRequest(): void {
    if (
      !this.canManageBusiness ||
      this.deletionForm.invalid ||
      this.selectedBusinessId === null ||
      this.deletionSubmitting
    ) {
      this.deletionForm.markAllAsTouched();
      return;
    }

    const businessId = this.selectedBusinessId;
    const reason = this.deletionForm.getRawValue().reason.trim();

    if (!reason) {
      this.deletionForm.controls.reason.setErrors({
        required: true
      });
      return;
    }

    this.deletionSubmitting = true;
    this.deletionError = '';
    this.deletionSuccess = '';

    this.businessApi.requestDeletion(businessId, reason).pipe(
      finalize(() => {
        this.deletionSubmitting = false;
      })
    ).subscribe({
      next: () => {
        this.deletionSuccess =
          'Deletion request submitted successfully.';

        this.closeDeletionForm();
        this.loadDeletionRequests();
      },
      error: (error: HttpErrorResponse) => {
        this.deletionError =
          error?.error?.message ||
          'Failed to submit deletion request.';
      }
    });
  }

  // ---------------- APPROVE / REJECT ----------------

  isReviewing(requestId: number): boolean {
    return this.reviewingRequestIds.has(requestId);
  }

  approveDeletionRequest(
    request: BusinessDeletionRequest
  ): void {
    if (
      !this.canReviewDeletionRequests ||
      this.isReviewing(request.id) ||
      request.status?.toUpperCase() !== 'PENDING'
    ) {
      return;
    }

    this.reviewRequest(request.id, 'approve');
  }

  rejectDeletionRequest(
    request: BusinessDeletionRequest
  ): void {
    if (
      !this.canReviewDeletionRequests ||
      this.isReviewing(request.id) ||
      request.status?.toUpperCase() !== 'PENDING'
    ) {
      return;
    }

    this.reviewRequest(request.id, 'reject');
  }

  private reviewRequest(
    requestId: number,
    action: 'approve' | 'reject'
  ): void {
    this.deletionError = '';
    this.deletionSuccess = '';

    this.reviewingRequestIds.add(requestId);

    const request$ =
      action === 'approve'
        ? this.businessApi.approveDeletionRequest(requestId)
        : this.businessApi.rejectDeletionRequest(requestId);

    request$.pipe(
      finalize(() => {
        this.reviewingRequestIds.delete(requestId);
      })
    ).subscribe({
      next: () => {
        this.deletionSuccess =
          action === 'approve'
            ? 'Deletion request approved successfully.'
            : 'Deletion request rejected successfully.';

        this.loadDeletionRequests();
        this.load();
      },
      error: (error: HttpErrorResponse) => {
        this.deletionError =
          error?.error?.message ||
          `Failed to ${action} deletion request.`;
      }
    });
  }
}