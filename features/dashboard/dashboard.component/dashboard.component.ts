import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';

import { selectUser } from '../../auth/store/auth.selector';
import { PageHeaderComponent } from '../../../shared/components/page-header.component/page-header.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    AsyncPipe,
    RouterLink,
    PageHeaderComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  private readonly store = inject(Store);

  readonly user$ =
    this.store.select(selectUser);
}
