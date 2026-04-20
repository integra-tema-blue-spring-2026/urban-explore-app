import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { Register } from './register';
import { AuthService } from '../../core/services/auth.service';

describe('Register', () => {
  let component: Register;
  let fixture: ComponentFixture<Register>;
  let authServiceSpy: {
    register: ReturnType<typeof vi.fn>;
  };
  let routerSpy: {
    navigateByUrl: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    authServiceSpy = {
      register: vi.fn(),
    };
    routerSpy = {
      navigateByUrl: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [
        { provide: AuthService, useValue: authServiceSpy as Partial<AuthService> },
        { provide: Router, useValue: routerSpy as Partial<Router> },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not submit when form is invalid', () => {
    (component as any).register();

    expect((component as any).submitted).toBe(true);
    expect(authServiceSpy.register).not.toHaveBeenCalled();
  });

  it('should register and navigate to cities when form is valid', () => {
    authServiceSpy.register.mockReturnValue(of(void 0));
    routerSpy.navigateByUrl.mockReturnValue(Promise.resolve(true));

    (component as any).registerForm.setValue({
      email: 'john@example.com',
      username: 'johndoe',
      password: 'password123',
      bio: {
        header: 'Hello',
        body: 'Traveler',
        footer: 'https://example.com',
      },
    });

    (component as any).register();

    expect(authServiceSpy.register).toHaveBeenCalledWith({
      email: 'john@example.com',
      username: 'johndoe',
      password: 'password123',
      bio: {
        header: 'Hello',
        body: 'Traveler',
        footer: 'https://example.com',
      },
    });
    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/cities');
  });

  it('should show alert when registration fails', () => {
    authServiceSpy.register.mockReturnValue(throwError(() => new Error('Bad request')));
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    (component as any).registerForm.setValue({
      email: 'john@example.com',
      username: 'johndoe',
      password: 'password123',
      bio: {
        header: 'Hello',
        body: 'Traveler',
        footer: 'https://example.com',
      },
    });

    (component as any).register();

    expect(consoleErrorSpy).toHaveBeenCalled();
    expect(alertSpy).toHaveBeenCalledWith('Registration failed.');
    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
  });
});
