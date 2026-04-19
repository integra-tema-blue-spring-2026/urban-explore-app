import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { poi } from '../utils/Poi-interface';

@Injectable({ providedIn: 'root' })
export class PoiService {
  private apiUrl = '/api/pois';
  private http = inject(HttpClient);

  getPois(cityId?: string): Observable<poi[]> {
    const params: any = {};
    if (cityId) {
      params.cityId = cityId;
    }
    return this.http.get<poi[]>(this.apiUrl, { params });
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
