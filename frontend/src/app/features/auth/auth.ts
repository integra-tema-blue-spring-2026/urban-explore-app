import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SelectButton } from 'primeng/selectbutton';
import { Register } from '../register/register';
import { Login } from '../login/login';

@Component({
  selector: 'app-auth',
  imports: [SelectButton, FormsModule, Register, Login],
  templateUrl: './auth.html',
  styleUrl: './auth.css',
})
export class Auth {
  protected selectedOption: any = 'register';
}
