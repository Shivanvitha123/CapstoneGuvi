import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Store } from '@ngrx/store';
import { TokenService } from './core/services/token.service';
import { loadCurrentUser } from './features/auth/store/auth.actions';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent {
  private readonly store=inject(Store);
  private readonly tokenService=inject(TokenService);
  constructor() {
    if(this.tokenService.hasToken()){
      this.store.dispatch(loadCurrentUser());
    }
  }
}
