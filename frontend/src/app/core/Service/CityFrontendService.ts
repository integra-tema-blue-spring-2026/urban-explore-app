import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { City } from '../../shared/models/CityFrontendModel';

@Injectable({
  providedIn: 'root'
})
export class CityService {
  private readonly apiUrl = 'http://localhost:8080/api/cities';
  constructor(private http: HttpClient) {}

  getCities(): Observable<City[]> {
    return this.http.get<City[]>(this.apiUrl);
  }
  addCity(city: City): Observable<City> {
    return this.http.post<City>(this.apiUrl, city);
  }
  updateCity(id: string, city: City): Observable<City> {
    return this.http.put<City>(`${this.apiUrl}/${id}`, city);
  }
  deleteCity(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
