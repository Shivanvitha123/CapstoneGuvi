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

import * as RiskActions
  from '../../store/risk.actions';

import {
  selectAssessments,
  selectSimulations,
  selectRiskError,
  selectRiskLoading,
  selectRiskSuccess
} from '../../store/risk.selectors';

import { TokenService }
  from '../../../../core/services/token.service';

@Component({
  selector: 'app-risk',
  standalone: true,

  imports: [
    CommonModule,
    ReactiveFormsModule,
    CurrencyPipe,
    DatePipe
  ],

  templateUrl: './risk.component.html',
  styleUrl: './risk.component.css',

  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RiskComponent implements OnInit {

  private readonly store = inject(Store);
  private readonly fb = inject(FormBuilder);
  private readonly tokenService = inject(TokenService);

  readonly userRole =
    (this.tokenService.getRole() ?? '')
      .toUpperCase()
      .replace(/^ROLE_/, '');

  // Authenticated users can view assessments.
  // Backend authorization must enforce this too.
  readonly canViewAssessments = true;

  // Only Risk Engineers can create assessments.
  readonly canManageAssessments =
    this.userRole === 'RISK_ENGINEER';

  // Only authorized roles can view simulations.
  readonly canViewSimulations =
    [
      'ADMIN',
      'UNDERWRITER',
      'RISK_ENGINEER'
    ].includes(this.userRole);

  // Only Admins and Risk Engineers can create simulations.
  readonly canManageSimulations =
    [
      'ADMIN',
      'RISK_ENGINEER'
    ].includes(this.userRole);

  // Risk assessments from NgRx store.
  readonly assessments$ =
    this.store.select(selectAssessments);

  // Risk simulations from NgRx store.
  readonly simulations$ =
    this.store.select(selectSimulations);

  // Loading state.
  readonly loading$ =
    this.store.select(selectRiskLoading);

  // Error message.
  readonly error$ =
    this.store.select(selectRiskError);

  // Success message.
  readonly success$ =
    this.store.select(selectRiskSuccess);

  activeTab: 'assessments' | 'simulations' =
    'assessments';

  showAssessmentForm = false;
  showSimulationForm = false;

  // Risk assessment form.
  readonly assessmentForm =
    this.fb.nonNullable.group({

      businessId: [
        0,
        [
          Validators.required,
          Validators.min(1)
        ]
      ],

      policyId: [0],

      riskScore: [
        0,
        [
          Validators.required,
          Validators.min(0),
          Validators.max(100)
        ]
      ],

      riskFactors: [''],
      recommendation: ['']
    });

  // Risk simulation form.
  readonly simulationForm =
    this.fb.nonNullable.group({

      businessId: [
        0,
        [
          Validators.required,
          Validators.min(1)
        ]
      ],

      policyId: [0],

      scenarioName: [
        '',
        Validators.required
      ],

      scenarioInput: ['']
    });

  ngOnInit(): void {

    // Clear old success and error messages.
    this.store.dispatch(
      RiskActions.clearRiskMessages()
    );

    // Load all risk assessments for viewing.
    // This allows Underwriters to see assessments
    // created by Risk Engineers.
    this.store.dispatch(
      RiskActions.loadAllAssessments()
    );

    // Load simulations only for authorized roles.
    if (this.canViewSimulations) {
      this.store.dispatch(
        RiskActions.loadSimulations()
      );
    }
  }

  // Switch between tabs.
  setTab(
    tab: 'assessments' | 'simulations'
  ): void {

    if (
      tab === 'simulations' &&
      !this.canViewSimulations
    ) {
      return;
    }

    this.activeTab = tab;
  }

  // Open the assessment form.
  // Only Risk Engineers can open it.
  openAssessmentForm(): void {

    if (!this.canManageAssessments) {
      return;
    }

    this.assessmentForm.reset({
      businessId: 0,
      policyId: 0,
      riskScore: 0,
      riskFactors: '',
      recommendation: ''
    });

    this.showAssessmentForm = true;
  }

  // Close the assessment form.
  closeAssessmentForm(): void {
    this.showAssessmentForm = false;
  }

  // Create a risk assessment.
  createAssessment(): void {

    // Prevent unauthorized assessment creation.
    if (!this.canManageAssessments) {
      return;
    }

    if (this.assessmentForm.invalid) {
      this.assessmentForm.markAllAsTouched();
      return;
    }

    const value =
      this.assessmentForm.getRawValue();

    this.store.dispatch(
      RiskActions.createAssessment({
        request: {
          businessId: value.businessId,
          policyId: value.policyId || null,
          riskScore: value.riskScore,
          riskFactors: value.riskFactors || '',
          recommendation: value.recommendation || ''
        }
      })
    );

    this.showAssessmentForm = false;
  }

  // Load assessments for a specific business
  // when needed elsewhere in the application.
  loadBusinessAssessments(
    businessId: number
  ): void {

    if (!businessId) {
      return;
    }

    this.store.dispatch(
      RiskActions.loadAssessments({
        businessId
      })
    );
  }

  // Open the simulation form.
  openSimulationForm(): void {

    if (!this.canManageSimulations) {
      return;
    }

    this.simulationForm.reset({
      businessId: 0,
      policyId: 0,
      scenarioName: '',
      scenarioInput: ''
    });

    this.showSimulationForm = true;
  }

  // Close the simulation form.
  closeSimulationForm(): void {
    this.showSimulationForm = false;
  }

  // Create a risk simulation.
  createSimulation(): void {

    if (!this.canManageSimulations) {
      return;
    }

    if (this.simulationForm.invalid) {
      this.simulationForm.markAllAsTouched();
      return;
    }

    const value =
      this.simulationForm.getRawValue();

    this.store.dispatch(
      RiskActions.createSimulation({
        request: {
          businessId: value.businessId,
          policyId: value.policyId || null,
          scenarioName: value.scenarioName,
          scenarioInput: value.scenarioInput || ''
        }
      })
    );

    this.showSimulationForm = false;
  }

  // CSS class for the risk-level badge.
  riskClass(
    level: string | null
  ): string {
    return level
      ? level.toLowerCase()
      : 'unknown';
  }
}