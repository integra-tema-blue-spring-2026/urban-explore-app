import {Injectable} from '@angular/core';
import {City} from '../model/city.model';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PointOfInterest, PointOfInterestFilter} from '../model/point-of-interest.model';

@Injectable({
  providedIn: 'root'
})
export class CityService {
  private apiPath = 'http://localhost:8080/api/cities';

  constructor(private httpClient: HttpClient) {}

  getCitiesWithName(name: string): Observable<City[]> {
    if(name === '' || name === null || name === undefined) {
      throw new Error('Name must not be empty');
    }

    return this.httpClient.get<City[]>(`${this.apiPath}/${name}`);
  }

  getPointsOfInterestFromCitiesWithName(name: string, filter: PointOfInterestFilter = {}): Observable<PointOfInterest[]> {
    if(name === '' || name === null || name === undefined) {
      throw new Error('Name must not be empty');
    }

    let params = new HttpParams();
    if (filter.name) {
      params = params.set('poiName', filter.name);
    }
    if (filter.description) {
      params = params.set('poiDescription', filter.description);
    }
    return this.httpClient.get<PointOfInterest[]>(`${this.apiPath}/${name}/pois`, { params });
  }
}
