import { Component, inject } from '@angular/core';
import {
  RouterLink,
  RouterLinkActive,
  RouterOutlet
} from '@angular/router';

import { AsyncPipe } from '@angular/common';
import { Store } from '@ngrx/store';


import * as Auth from '../../../features/auth/store/auth.actions';
import { selectUser } from '../../../features/auth/store/auth.selector';
import { NotificationComponent } from '../../../shared/notification/notification.component/notification.component';
import { RoleLabelPipe } from '../../../shared/pipes/role-label.pipe';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    AsyncPipe,
    RoleLabelPipe,
    NotificationComponent
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css'
})
export class MainLayoutComponent {
  private readonly store = inject(Store);

  readonly user$ =
    this.store.select(selectUser);

  logout(): void {
    this.store.dispatch(Auth.logout());
  }
}

