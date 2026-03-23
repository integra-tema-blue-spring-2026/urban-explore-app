import {inject, Injectable} from '@angular/core';
import {City} from '../../../shared/models/city.model';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {PointOfInterest, PointOfInterestFilter} from '../../../shared/models/point-of-interest.model';

@Injectable({
  providedIn: 'root'
})
export class CityService {
  private apiPath = '/api/cities';
  private httpClient = inject(HttpClient);

  getCitiesWithName(name: string): Observable<City[]> {
    if(!name.trim()) {
      return throwError(() => new Error('Name must not be empty'));
    }

    return this.httpClient.get<City[]>(`${this.apiPath}/${encodeURIComponent(name)}`);
  }

  getPointsOfInterestFromCitiesWithName(name: string, filter: PointOfInterestFilter = {}): Observable<PointOfInterest[]> {
    if(!name.trim()) {
      return throwError(() => new Error('Name must not be empty'));
    }

    let params = new HttpParams();
    if (filter.name) {
      params = params.set('poiName', filter.name);
    }

    if (filter.description) {
      params = params.set('poiDescription', filter.description);
    }

    return this.httpClient.get<PointOfInterest[]>(`${this.apiPath}/${encodeURIComponent(name)}/pois`, { params });
  }
}
