import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SelectButton } from 'primeng/selectbutton';
import { Register } from '../register/register';
import { Login } from '../login/login';

type AuthOption = 'register' | 'login';
type AuthOptionItem = {
  label: string;
  value: AuthOption;
};

@Component({
  selector: 'app-auth',
  imports: [SelectButton, FormsModule, Register, Login],
  templateUrl: './auth.html',
  styleUrl: './auth.css',
})
export class Auth {
  protected readonly options: Array<AuthOptionItem> = [
    { label: 'Register', value: 'register' },
    { label: 'Login', value: 'login' },
  ];

  protected selectedOption: AuthOption = 'register';
}
