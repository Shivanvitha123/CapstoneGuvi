import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { RouterLink } from '@angular/router';
import { AsyncPipe } from '@angular/common';
import { Store } from '@ngrx/store';

import * as Auth from '../../../store/auth.actions';
import {
  selectError,
  selectLoading
} from '../../../store/auth.selector';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    AsyncPipe
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly store = inject(Store);

  readonly loading$ =
    this.store.select(selectLoading);

  readonly error$ =
    this.store.select(selectError);

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [
      Validators.required,
      Validators.minLength(6)
    ]]
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.store.dispatch(
      Auth.register({
        request: this.form.getRawValue()
      })
    );
  }
}
