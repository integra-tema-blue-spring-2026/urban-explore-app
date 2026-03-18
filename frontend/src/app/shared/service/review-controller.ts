import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReviewDto } from '../models/reviewDto';
import { ReviewCreateDto } from '../models/reviewCreateDto';
import { ReviewUpdateDto } from '../models/reviewUpdateDto';
@Injectable({
  providedIn: 'root',
})
export class ReviewController {
  private readonly apiUrl = '/api/reviews';

    constructor(private http: HttpClient) {
    }

    public addReview(review: ReviewCreateDto): Observable<ReviewDto> {
      return this.http.post<ReviewDto>(this.apiUrl, review);
    }

    public updateReview(id: string, review: ReviewUpdateDto): Observable<ReviewDto> {
      return this.http.put<ReviewDto>(`${this.apiUrl}/${id}`, review);
    }

    public deleteReview(id: string): Observable<void> {
      return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    public getReviews(): Observable<ReviewDto[]> {
      return this.http.get<ReviewDto[]>(this.apiUrl);
    }
}