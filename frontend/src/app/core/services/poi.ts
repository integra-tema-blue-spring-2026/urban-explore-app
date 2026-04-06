import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { poi } from '../utils/Poi-interface';

@Injectable({ providedIn: 'root' })
export class PoiService {
  private apiUrl = 'http://localhost:8080/api/pois';

  constructor(private http: HttpClient) {}

  getPois(): Observable<poi[]> {
    return this.http.get<poi[]>(this.apiUrl);
  }

  addPoi(poiData: poi): Observable<poi> {
    return this.http.post<poi>(this.apiUrl, poiData);
  }

  getPoiById(id: string): Observable<poi> {
    return this.http.get<poi>(`${this.apiUrl}/${id}`);
  }

  updatePoi(id: string, poiData: poi): Observable<poi> {
    return this.http.put<poi>(`${this.apiUrl}/${id}`, poiData);
  }

  deletePoi(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
