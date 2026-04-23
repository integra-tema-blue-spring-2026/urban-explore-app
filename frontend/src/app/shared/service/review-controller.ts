import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReviewDto } from '../models/reviewDto';
import { ReviewCreateDto } from '../models/reviewCreateDto';
import { ReviewUpdateDto } from '../models/reviewUpdateDto';
import {UserProfileReviewDto} from '../models/user/user-profile-review.dto';
@Injectable({
  providedIn: 'root',
})
export class ReviewController {
  private http = inject(HttpClient);
  private readonly apiUrl = '/api/reviews';

    public addReview(review: ReviewCreateDto): Observable<ReviewDto> {
      return this.http.post<ReviewDto>(this.apiUrl, review);
    }

    public getUserProfileReviews(userId: string): Observable<UserProfileReviewDto[]> {
      return this.http.get<UserProfileReviewDto[]>(`${this.apiUrl}/user/${userId}/profile-view`);
    }

    public updateReview(id: string, review: ReviewUpdateDto): Observable<ReviewDto> {
      return this.http.put<ReviewDto>(`${this.apiUrl}/${id}`, review);
    }

    public deleteReview(id: string): Observable<void> {
      return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    public getAllReviews(): Observable<ReviewDto[]> {
      return this.http.get<ReviewDto[]>(this.apiUrl);
    }

    public getFilteredReviews(poiId?: string | null, userId?: string | null): Observable<ReviewDto[]> {
      let params: any = {};
      if (poiId) params.poiId = poiId;
      if (userId) params.userId = userId;
      console.log('Fetching reviews with params:', params);
      return this.http.get<ReviewDto[]>(this.apiUrl, { params });
    }


  }

