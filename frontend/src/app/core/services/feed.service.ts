import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ActivityDto } from '../../shared/models/activity/Activity';

@Injectable({
  providedIn: 'root'
})
export class FeedService {
  private apiUrl = '/api/feed';

  constructor(private http: HttpClient) {}

  getFeed(): Observable<ActivityDto[]> {
    return this.http.get<ActivityDto[]>(this.apiUrl);
  }
}
