import {inject, Injectable} from '@angular/core';
import {User} from '../../shared/models/user/user';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable} from 'rxjs';

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
  updateUser(userId : string, userData : Partial<User>) : Observable<User>{
    return this.http.put<User>(`${this.USER_API_URL}/${userId}`, userData);
  }

  followUser(userId : string, followerId: string) : Observable<User>{
    return this.http.put<User>(`${this.USER_API_URL}/${userId}/follow`, {followerId});
  }

  unfollowUser(userId : string, followerId: string) : Observable<User>{
    return this.http.put<User>(`${this.USER_API_URL}/${userId}/unfollow`, {followerId});
  }

  //POST ------------------------------------------------------
  createUser(userData : Partial<User>, password: string) : Observable<User>{
    return this.http.post<User>(`${this.USER_API_URL}`, {...userData, password});
  }

  //DELETE ------------------------------------------------------
  deleteUser(userId : string) : Observable<void>{
    return this.http.delete<void>(`${this.USER_API_URL}/${userId}`);
  }

}
