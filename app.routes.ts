import { Routes } from '@angular/router';

import { WelcomeComponent } from './features/welcome/welcome.component';

import { AuthLayoutComponent } from './layout/authLayout/auth-layout.component/auth-layout.component';
import { LoginComponent } from './features/auth/pages/login/login.component/login.component';
import { RegisterComponent } from './features/auth/pages/register/register.component/register.component';

import { authGuard } from './core/guards/auth.guard';

import { MainLayoutComponent } from './layout/mainLayout/main-layout.component/main-layout.component';
import { DashboardComponent } from './features/dashboard/dashboard.component/dashboard.component';
import { BusinessComponent } from './features/business/components/business.component/business.component';
import { PoliciesComponent } from './features/policies/pages/policies.component/policies.component';
import { RiskComponent } from './features/risk/pages/risk.component/risk.component';
import { ClaimsComponent } from './features/claims/pages/claims.component/claims.component';
import { NotificationComponent } from './shared/notification/notification.component/notification.component';

export const routes: Routes = [
  // Public welcome page - opening page
  {
    path: '',
    component: WelcomeComponent,
    pathMatch: 'full'
  },

  // Login and registration pages
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      {
        path: 'login',
        component: LoginComponent
      },
      {
        path: 'register',
        component: RegisterComponent
      }
    ]
  },

  // Protected application pages
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent
      },
      {
        path: 'business',
        component: BusinessComponent
      },
      {
        path: 'policies',
        component: PoliciesComponent
      },
      {
        path: 'risk',
        component: RiskComponent
      },
      {
        path: 'claims',
        component: ClaimsComponent
      },
      {
        path: 'notifications',
        component: NotificationComponent
      }
    ]
  },

  // Redirect unknown routes to the welcome page
  {
    path: '**',
    redirectTo: ''
  }
];