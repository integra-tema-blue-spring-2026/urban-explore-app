import { inject, Injectable } from '@angular/core';
import { map, Observable, switchMap, tap } from 'rxjs';
import { UserRegisterDto } from '../../shared/models/user/user-register-dto';
import { UserService } from '../../features/profile/user.service';
import { UserLoginDto } from '../../shared/models/user/user-login.dto';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly userService: UserService = inject(UserService);

  register(userData: UserRegisterDto): Observable<void> {
    return this.userService
      .registerUser(userData)
      .pipe(
        switchMap(() => this.login({ username: userData.username, password: userData.password })),
      );
  }

  login(userData: UserLoginDto): Observable<void> {
    return this.userService.loginUser(userData).pipe(
      tap((response) => {
        localStorage.setItem('token', response.token);
      }),
      map(() => void 0),
    );
  }
}
