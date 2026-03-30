import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { AuthService } from './auth.service';
import { UserService } from '../../features/profile/user.service';
import { UserRole } from '../../shared/models/user/user-role';

describe('AuthService', () => {
  let service: AuthService;
  let userServiceSpy: {
    registerUser: ReturnType<typeof vi.fn>;
    loginUser: ReturnType<typeof vi.fn>;
  };

  beforeEach(() => {
    userServiceSpy = {
      registerUser: vi.fn(),
      loginUser: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: UserService, useValue: userServiceSpy as Partial<UserService> },
      ]
    });
  afterEach(() => {
    vi.restoreAllMocks();
    vi.unstubAllGlobals();
  });

    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login and store token', () => {
    const credentials = { username: 'johndoe', password: 'password123' };
    const tokenResponse = { token: 'mock-jwt-token' };
    const setItemSpy = vi.fn();
    vi.stubGlobal('localStorage', {
      setItem: setItemSpy,
    });

    userServiceSpy.loginUser.mockReturnValue(of(tokenResponse));

    service.login(credentials).subscribe((result) => {
      expect(result).toBeUndefined();
    });

    expect(userServiceSpy.loginUser).toHaveBeenCalledWith(credentials);
    expect(setItemSpy).toHaveBeenCalledWith('token', tokenResponse.token);
  });

  it('should register, then login and store token', () => {
    const userData = {
      email: 'john@example.com',
      username: 'johndoe',
      password: 'password123',
      bio: {
        header: 'Hello',
        body: 'Traveler',
        footer: 'https://example.com',
      },
    };
    const setItemSpy = vi.fn();
    vi.stubGlobal('localStorage', {
      setItem: setItemSpy,
    });

    userServiceSpy.registerUser.mockReturnValue(of({
      id: '123e4567-e89b-12d3-a456-426614174000',
      email: userData.email,
      username: userData.username,
      role: UserRole.USER,
      bio: {
        header: 'Hello',
        body: 'Traveler',
        footer: 'https://example.com',
      },
      avatarUrl: '',
    }));
    userServiceSpy.loginUser.mockReturnValue(of({ token: 'registered-token' }));

    service.register(userData).subscribe((result) => {
      expect(result).toBeUndefined();
    });

    expect(userServiceSpy.registerUser).toHaveBeenCalledWith(userData);
    expect(userServiceSpy.loginUser).toHaveBeenCalledWith({
      username: userData.username,
      password: userData.password,
    });
    expect(setItemSpy).toHaveBeenCalledWith('token', 'registered-token');
  });
});
