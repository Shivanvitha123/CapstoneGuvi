import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject
} from '@angular/core';

import {
  CommonModule,
  CurrencyPipe,
  DatePipe
} from '@angular/common';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { Store } from '@ngrx/store';

import { StatusCountPipe } from '../../../../shared/pipes/status-count.pipe';

import * as PolicyActions from '../../store/policy.actions';

import {
  selectPolicies,
  selectPolicyError,
  selectPolicyLoading,
  selectPolicySuccessMessage
} from '../../store/policy.selectors';

import {
  Policy,
  CreatePolicyRequest,
  UpdatePolicyRequest
} from '../../models/policy.models';

@Component({
  selector: 'app-policies',
  standalone: true,

  imports: [
    CommonModule,
    ReactiveFormsModule,
    CurrencyPipe,
    DatePipe,
    StatusCountPipe
  ],

  templateUrl: './policies.component.html',
  styleUrl: './policies.component.css',

  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PoliciesComponent implements OnInit {

  private readonly store = inject(Store);
  private readonly fb = inject(FormBuilder);

  readonly policies$ =
    this.store.select(selectPolicies);

  readonly loading$ =
    this.store.select(selectPolicyLoading);

  readonly error$ =
    this.store.select(selectPolicyError);

  readonly successMessage$ =
    this.store.select(selectPolicySuccessMessage);

  readonly statuses = [
    'DRAFT',
    'SUBMITTED',
    'UNDER_REVIEW',
    'APPROVED',
    'REJECTED',
    'ACTIVE',
    'EXPIRED'
  ];

  readonly policyForm = this.fb.nonNullable.group({
    businessId: [
      0,
      [
        Validators.required,
        Validators.min(1)
      ]
    ],

    policyNumber: [
      '',
      Validators.required
    ],

    policyType: [
      '',
      Validators.required
    ],

    coverageAmount: [
      0,
      [
        Validators.required,
        Validators.min(0.01)
      ]
    ],

    premiumAmount: [
      0,
      [
        Validators.required,
        Validators.min(0.01)
      ]
    ],

    startDate: [
      '',
      Validators.required
    ],

    endDate: [
      '',
      Validators.required
    ],

    description: [
      ''
    ]
  });

  showForm = false;

  editingPolicy: Policy | null = null;

  selectedPolicy: Policy | null = null;

  currentRole = '';

  ngOnInit(): void {

    this.currentRole =
      this.getRoleFromToken();

    if (this.currentRole === 'BUSINESS_OWNER') {

      this.store.dispatch(
        PolicyActions.loadOwnerPolicies()
      );

    } else {

      this.store.dispatch(
        PolicyActions.loadAllPolicies()
      );
    }
  }

  openCreate(): void {

    this.editingPolicy = null;
    this.selectedPolicy = null;

    this.policyForm.reset({
      businessId: 0,
      policyNumber: '',
      policyType: '',
      coverageAmount: 0,
      premiumAmount: 0,
      startDate: '',
      endDate: '',
      description: ''
    });

    this.showForm = true;
  }

  openEdit(policy: Policy): void {

    this.editingPolicy = policy;
    this.selectedPolicy = null;

    this.policyForm.patchValue({
      businessId: policy.businessId,
      policyNumber: policy.policyNumber,
      policyType: policy.policyType,
      coverageAmount: policy.coverageAmount,
      premiumAmount: policy.premiumAmount,
      startDate: policy.startDate,
      endDate: policy.endDate,
      description: policy.description ?? ''
    });

    this.showForm = true;
  }

  closeForm(): void {

    this.showForm = false;
    this.editingPolicy = null;
  }

  viewPolicy(policy: Policy): void {

    this.selectedPolicy =
      this.selectedPolicy?.id === policy.id
        ? null
        : policy;
  }

  savePolicy(): void {

    if (this.policyForm.invalid) {

      this.policyForm.markAllAsTouched();

      return;
    }

    const value =
      this.policyForm.getRawValue();

    if (this.editingPolicy) {

      const request: UpdatePolicyRequest = {

        businessId: value.businessId,

        policyNumber:
          value.policyNumber,

        policyType:
          value.policyType,

        coverageAmount:
          value.coverageAmount,

        premiumAmount:
          value.premiumAmount,

        startDate:
          value.startDate,

        endDate:
          value.endDate,

        description:
          value.description
      };

      this.store.dispatch(
        PolicyActions.updatePolicy({
          policyId:
            this.editingPolicy.id,

          request
        })
      );

    } else {

      const request: CreatePolicyRequest = {

        businessId:
          value.businessId,

        policyNumber:
          value.policyNumber,

        policyType:
          value.policyType,

        coverageAmount:
          value.coverageAmount,

        premiumAmount:
          value.premiumAmount,

        startDate:
          value.startDate,

        endDate:
          value.endDate,

        description:
          value.description
      };

      this.store.dispatch(
        PolicyActions.createPolicy({
          request
        })
      );
    }

    this.showForm = false;
    this.editingPolicy = null;
  }

  submit(policy: Policy): void {

    if (
      !confirm(
        `Submit policy ${policy.policyNumber} for review?`
      )
    ) {
      return;
    }

    this.store.dispatch(
      PolicyActions.submitPolicy({
        policyId: policy.id
      })
    );
  }

  changeStatus(
    policy: Policy,
    status: string
  ): void {

    this.store.dispatch(
      PolicyActions.updatePolicyStatus({
        policyId: policy.id,
        status
      })
    );
  }

  canCreate(): boolean {

    return [
      'BUSINESS_OWNER',
      'ADMIN'
    ].includes(this.currentRole);
  }

  canEdit(policy: Policy): boolean {

    return (
      this.currentRole === 'ADMIN' ||
      (
        this.currentRole === 'BUSINESS_OWNER' &&
        policy.status === 'DRAFT'
      )
    );
  }

  canSubmit(policy: Policy): boolean {

    return (
      (
        this.currentRole === 'BUSINESS_OWNER' ||
        this.currentRole === 'ADMIN'
      ) &&
      policy.status === 'DRAFT'
    );
  }

  canChangeStatus(): boolean {

    return [
      'ADMIN',
      'UNDERWRITER'
    ].includes(this.currentRole);
  }

  getStatusClass(status: string): string {

    return `status-${status.toLowerCase()}`;
  }

  private getRoleFromToken(): string {

    try {

      const token =
        localStorage.getItem(
          'risk_twin_token'
        );

      if (!token) {
        return '';
      }

      const payload =
        JSON.parse(
          atob(
            token
              .split('.')[1]
              .replace(/-/g, '+')
              .replace(/_/g, '/')
          )
        );

      return payload.role ?? '';

    } catch {

      return '';
    }
  }
}
