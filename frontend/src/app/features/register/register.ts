import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { AuthService } from '../../core/services/auth.service';
import { TextareaModule } from 'primeng/textarea';
import { SelectButton } from 'primeng/selectbutton';
import { UserRole } from '../../shared/models/user/user-role';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, InputTextModule, ButtonModule, TextareaModule, SelectButton],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  protected submitted = false;
  protected registerForm: FormGroup<{
    email: FormControl<string>;
    username: FormControl<string>;
    password: FormControl<string>;
    role: FormControl<UserRole>;
    bio: FormGroup<{
      header: FormControl<string>;
      body: FormControl<string>;
      footer: FormControl<string>;
    }>;
  }> = new FormGroup({
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
    username: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(6), Validators.maxLength(40)],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(6)],
    }),
    role: new FormControl<UserRole>(UserRole.USER, {
      nonNullable: true,
      validators: [Validators.required],
    }),
    bio: new FormGroup({
      header: new FormControl('', { nonNullable: true, validators: [] }),
      body: new FormControl('', { nonNullable: true, validators: [] }),
      footer: new FormControl('', { nonNullable: true, validators: [] }),
    }),
  });

  protected readonly roleOptions = [
    { label: 'User', value: UserRole.USER },
    { label: 'Admin', value: UserRole.ADMIN },
  ];

  protected register() {
    this.submitted = true;

    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.authService.register(this.registerForm.getRawValue()).subscribe({
      next: () => {
        this.router.navigateByUrl('/cities');
      },
      error: (error) => {
        console.error('Registration failed:', error);
        alert('Registration failed.');
      },
    });
  }
}
