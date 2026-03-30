import {inject, Injectable} from '@angular/core';
import {User} from '../../shared/models/user/user';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable} from 'rxjs';
import {UserRegisterDto} from '../../shared/models/user/user-register-dto';
import {UserUpdateDto} from '../../shared/models/user/user-update-dto';
import {UserLoginDto} from '../../shared/models/user/user-login.dto';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  http = inject(HttpClient);

  USER_API_URL = `${environment.apiUrl}/users`;

  //GET------------------------------------------------------
  getUser(userId : string) : Observable<User>{
      return this.http.get<User>(`${this.USER_API_URL}/${userId}`)
  }

  getUserFollowers(userId : string) : Observable<User[]>{
    return this.http.get<User[]>(`${this.USER_API_URL}/${userId}/followers`);
  }

  getUserFollowing(userId : string) : Observable<User[]>{
    return this.http.get<User[]>(`${this.USER_API_URL}/${userId}/following`);
  }

  //PUT ------------------------------------------------------
  updateUser(userId : string, userData : UserUpdateDto) : Observable<User>{
    return this.http.put<User>(`${this.USER_API_URL}/${userId}`, userData);
  }

  followUser(userId : string, followerId: string) : Observable<User>{
    return this.http.put<User>(`${this.USER_API_URL}/follow/${userId}`,
      {},
      {
        params: { followerId: followerId }
      });
  }

  unfollowUser(userId : string, followerId: string) : Observable<User>{
    return this.http.put<User>(`${this.USER_API_URL}/unfollow/${userId}`,{},
      {
        params: { followerId: followerId }
      });
  }

  //POST ------------------------------------------------------
  registerUser(userData : UserRegisterDto) : Observable<User>{
    return this.http.post<User>(`${this.USER_API_URL}/auth/register`, userData);
  }

  loginUser(userData: UserLoginDto) : Observable<{token: string}> {
    return this.http.post<{token: string}>(`${this.USER_API_URL}/auth/login`, userData);
  }

  //DELETE ------------------------------------------------------
  deleteUser(userId : string) : Observable<void>{
    return this.http.delete<void>(`${this.USER_API_URL}/${userId}`);
  }

}
