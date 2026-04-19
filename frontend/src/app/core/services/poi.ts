import { inject, Injectable } from '@angular/core';
import { from, Observable, of, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import {
  PointOfInterestControllerService,
  PointOfInterestRequestDto,
  PointOfInterestResponseDto,
  GetPointsOfInterestWithFilterRequestParams
} from '../api/generated';

type PoiFilter = GetPointsOfInterestWithFilterRequestParams

@Injectable({ providedIn: 'root' })
export class PoiService {
  private pointOfInterestController = inject(PointOfInterestControllerService);

  private isPoiResponse(value: unknown): value is PointOfInterestResponseDto {
    return !!value && typeof value === 'object' && !Array.isArray(value) && !(value instanceof Blob);
  }

  private normalizePoiListResponse(response: unknown): Observable<PointOfInterestResponseDto[]> {
    if (Array.isArray(response)) {
      return of(response as PointOfInterestResponseDto[]);
    }

    if (response instanceof Blob) {
      return from(response.text()).pipe(
        switchMap((text) => {
          try {
            const parsed = JSON.parse(text);
            return of(Array.isArray(parsed) ? (parsed as PointOfInterestResponseDto[]) : []);
          } catch {
            return of([]);
          }
        }),
      );
    }

    return of([]);
  }

  private normalizePoiResponse(response: unknown): Observable<PointOfInterestResponseDto> {
    if (this.isPoiResponse(response)) {
      return of(response);
    }

    if (response instanceof Blob) {
      return from(response.text()).pipe(
        switchMap((text) => {
          try {
            const parsed = JSON.parse(text);
            if (this.isPoiResponse(parsed)) {
              return of(parsed);
            }

            const maybeWrapped = (parsed as { data?: unknown })?.data;
            if (this.isPoiResponse(maybeWrapped)) {
              return of(maybeWrapped);
            }
          } catch {

          }

          return throwError(() => new Error('Invalid POI details response'));
        }),
      );
    }

    return throwError(() => new Error('Invalid POI details response'));
  }

  getPois(filter:PoiFilter = {}): Observable<PointOfInterestResponseDto[]> {
    return this.pointOfInterestController
      .getPointsOfInterestWithFilter(filter, 'body', false, { httpHeaderAccept: 'application/json' as any })
      .pipe(
        switchMap((response) => this.normalizePoiListResponse(response)),
        catchError(() => of([])),
      );
  }

  addPoi(poiData: PointOfInterestRequestDto): Observable<PointOfInterestResponseDto> {
    return this.pointOfInterestController.savePointOfInterest({
      pointOfInterestRequestDto: poiData,
    });
  }

  getPoiById(id: string): Observable<PointOfInterestResponseDto> {
    return this.pointOfInterestController
      .findById({ id }, 'body', false, { httpHeaderAccept: 'application/json' as any })
      .pipe(switchMap((response) => this.normalizePoiResponse(response)));
  }

  updatePoi(id: string, poiData: PointOfInterestRequestDto): Observable<PointOfInterestResponseDto> {
    return this.pointOfInterestController.update1({
      id,
      pointOfInterestRequestDto: poiData,
    });
  }

  deletePoi(id: string): Observable<void> {
    return this.pointOfInterestController.deleteById({ id });
  }
}
