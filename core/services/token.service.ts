import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private readonly key = 'risk_twin_token';

  set(token: string): void {
    localStorage.setItem(this.key, token);
  }

  get(): string | null {
    return localStorage.getItem(this.key);
  }

  clear(): void {
    localStorage.removeItem(this.key);
  }

  hasToken(): boolean {
    return !!this.get();
  }

  payload(): Record<string, any> | null {
    try {
      const token = this.get();

      if (!token) {
        return null;
      }

      const parts = token.split('.');

      if (parts.length !== 3) {
        return null;
      }

      const encodedPayload = parts[1]
        .replace(/-/g, '+')
        .replace(/_/g, '/');

      const paddedPayload = encodedPayload.padEnd(
        Math.ceil(encodedPayload.length / 4) * 4,
        '='
      );

      return JSON.parse(atob(paddedPayload));
    } catch {
      return null;
    }
  }

  getRole(): string | null {
    const payload = this.payload();

    if (!payload) {
      return null;
    }

    const role =
      payload['role'] ??
      payload['roles'] ??
      payload['authorities'] ??
      payload['scope'];

    if (Array.isArray(role)) {
      const firstRole = role[0];

      if (typeof firstRole === 'string') {
        return firstRole
          .replace(/^ROLE_/, '')
          .toUpperCase();
      }

      if (firstRole && typeof firstRole === 'object') {
        const value = firstRole.authority ?? firstRole.name;

        return typeof value === 'string'
          ? value.replace(/^ROLE_/, '').toUpperCase()
          : null;
      }

      return null;
    }

    if (typeof role === 'string') {
      return role
        .split(/[ ,]+/)[0]
        .replace(/^ROLE_/, '')
        .toUpperCase();
    }

    return null;
  }

  getBusinessId(): number | null {
    const payload = this.payload();

    if (!payload) {
      return null;
    }

    const possibleId =
      payload['businessId'] ??
      payload['business_id'] ??
      payload['businessID'] ??
      payload['business']?.['id'];

    const businessId = Number(possibleId);

    return Number.isInteger(businessId) && businessId > 0
      ? businessId
      : null;
  }
}