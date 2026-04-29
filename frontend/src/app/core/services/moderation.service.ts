import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type ModerationEntity = 'city' | 'poi';
export type ModerationStatus = 'APPROVED' | 'REJECTED';

@Injectable({
  providedIn: 'root',
})
export class ModerationService {
  private readonly http = inject(HttpClient);
  private readonly apiPath = '/api/moderation';

  updateStatus(entity: ModerationEntity, id: string, status: ModerationStatus): Observable<void> {
    return this.http.patch<void>(`${this.apiPath}/${entity}/${id}/status`, status);
  }
}
