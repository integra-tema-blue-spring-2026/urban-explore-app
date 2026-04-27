import { inject, Injectable } from '@angular/core';
import { from, Observable, of, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import {
  CityControllerService,
  CityDto,
  CreateCityDto,
  UpdateCityDto,
} from '../generated';

@Injectable({
  providedIn: 'root',
})
export class CityService {
  private cityController = inject(CityControllerService);

  private normalizeCityListResponse(response: unknown): Observable<CityDto[]> {
    if (Array.isArray(response)) {
      return of(response as CityDto[]);
    }

    if (response instanceof Blob) {
      return from(response.text()).pipe(
        switchMap((text) => {
          try {
            const parsed = JSON.parse(text);
            return of(Array.isArray(parsed) ? (parsed as CityDto[]) : []);
          } catch {
            return of([]);
          }
        }),
      );
    }

    return of([]);
  }

  private normalizeSingleCityResponse(response: unknown): Observable<CityDto> {
    if (response && typeof response === 'object' && !Array.isArray(response) && !(response instanceof Blob)) {
      return of(response as CityDto);
    }

    if (response instanceof Blob) {
      return from(response.text()).pipe(
        switchMap((text) => {
          try {
            const parsed = JSON.parse(text);
            return of(parsed as CityDto);
          } catch {
            return throwError(() => new Error('Failed to parse City JSON from Blob'));
          }
        })
      );
    }

    return throwError(() => new Error('Invalid response format for CityDto'));
  }

  getCitiesWithName(cityName: string): Observable<CityDto[]> {
    if (!cityName.trim()) {
      return throwError(() => new Error('Name must not be empty'));
    }

    return this.cityController
      .getAllCities({name: cityName}, 'body', false, { httpHeaderAccept: 'application/json' as any })
      .pipe(
        switchMap((response) => this.normalizeCityListResponse(response)),
        catchError(() => of([])),
      );
  }

  getCities(): Observable<CityDto[]> {
    return this.cityController
      .getAllCities({}, 'body', false, { httpHeaderAccept: 'application/json' as any })
      .pipe(
        switchMap((response) => this.normalizeCityListResponse(response)),
        catchError(() => of([])),
      );
  }
  addCity(city: CreateCityDto): Observable<CityDto> {
    return this.cityController.createCity({ createCityDto: city });
  }
  updateCity(id: string, city: UpdateCityDto): Observable<CityDto> {
    return this.cityController.updateCity({ id: id, updateCityDto: city });
  }
  deleteCity(id: string): Observable<void> {
    return this.cityController.deleteCity({ id: id });
  }
  getCityById(id: string): Observable<CityDto> {
    return this.cityController.getCityById({ id: id }, 'body', false, { httpHeaderAccept: 'application/json' as any })
      .pipe(
        switchMap((response) => this.normalizeSingleCityResponse(response)),
        catchError((error) => {
          console.error('Error fetching city by ID:', error);
          return throwError(() => error);
        })
      );
  }
}
