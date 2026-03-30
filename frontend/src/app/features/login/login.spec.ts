import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { Login } from './login';
import { AuthService } from '../../core/services/auth.service';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let authServiceSpy: {
    login: ReturnType<typeof vi.fn>;
  };
  let routerSpy: {
    navigateByUrl: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    authServiceSpy = {
      login: vi.fn(),
    };
    routerSpy = {
      navigateByUrl: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        { provide: AuthService, useValue: authServiceSpy as Partial<AuthService> },
        { provide: Router, useValue: routerSpy as Partial<Router> },
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Login);
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
    (component as any).login();

    expect((component as any).submitted).toBe(true);
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('should login and navigate to cities when form is valid', () => {
    authServiceSpy.login.mockReturnValue(of(void 0));
    routerSpy.navigateByUrl.mockReturnValue(Promise.resolve(true));

    (component as any).loginForm.setValue({
      username: 'johndoe',
      password: 'password123',
    });

    (component as any).login();

    expect(authServiceSpy.login).toHaveBeenCalledWith({
      username: 'johndoe',
      password: 'password123',
    });
    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/cities');
  });

  it('should show alert when login fails', () => {
    authServiceSpy.login.mockReturnValue(throwError(() => new Error('Unauthorized')));
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    (component as any).loginForm.setValue({
      username: 'johndoe',
      password: 'password123',
    });

    (component as any).login();

    expect(consoleErrorSpy).toHaveBeenCalled();
    expect(alertSpy).toHaveBeenCalledWith('Login failed.');
    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
  });
});
