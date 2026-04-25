import { Injectable } from '@angular/core';
import { UserRole } from '../../shared/models/user/user-role';

type JwtPayload = {
  role?: string;
};

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  getToken(): string | null {
    const token = localStorage.getItem('token');
    if (typeof token !== 'string' || token.trim().length === 0) {
      return null;
    }
    return token;
  }

  hasToken(): boolean {
    return this.getToken() !== null;
  }

  getRole(): UserRole | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }

    const payload = this.parseJwtPayload(token);
    if (!payload?.role) {
      return null;
    }

    const normalizedRole = payload.role.toUpperCase();
    if (normalizedRole === UserRole.ADMIN) {
      return UserRole.ADMIN;
    }

    if (normalizedRole === UserRole.USER) {
      return UserRole.USER;
    }

    return null;
  }

  isAdmin(): boolean {
    return this.getRole() === UserRole.ADMIN;
  }

  private parseJwtPayload(token: string): JwtPayload | null {
    const tokenParts = token.split('.');
    if (tokenParts.length < 2) {
      return null;
    }

    const base64 = tokenParts[1].replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');

    try {
      const decoded = atob(padded);
      return JSON.parse(decoded) as JwtPayload;
    } catch {
      return null;
    }
  }
}
