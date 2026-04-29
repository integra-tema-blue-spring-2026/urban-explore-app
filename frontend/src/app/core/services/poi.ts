import {inject, Injectable} from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { poi } from '../utils/Poi-interface';

@Injectable({ providedIn: 'root' })
export class PoiService {
  private apiUrl = '/api/pois';
  http = inject(HttpClient);

  getPoisByStatus(status: string): Observable<poi[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<poi[]>(this.apiUrl, { params });
  }

  getPendingPois(): Observable<poi[]> {
    return this.http.get<poi[]>(`${this.apiUrl}/pending`);
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
