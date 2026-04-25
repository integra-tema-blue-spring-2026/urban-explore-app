import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, forkJoin, finalize, of } from 'rxjs';
import { CityService } from '../../../core/api/services/city.service';
import { City } from '../../../shared/models/city.model';
import { PoiService } from '../../../core/services/poi';
import { poi } from '../../../core/utils/Poi-interface';
import { ModerationEntity, ModerationService, ModerationStatus } from '../../../core/services/moderation.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIf, NgFor],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboardComponent implements OnInit {
  private readonly cityService = inject(CityService);
  private readonly poiService = inject(PoiService);
  private readonly moderationService = inject(ModerationService);
  private cdr = inject(ChangeDetectorRef);

  pendingCities: City[] = [];
  pendingPois: poi[] = [];
  loading = false;
  errorMessage = '';
  protected readonly statusOptions: Array<{ label: string; value: ModerationStatus }> = [
    { label: 'Approve', value: 'APPROVED' },
    { label: 'Reject', value: 'REJECTED' },
  ];

  protected cityStatusDrafts: Record<string, ModerationStatus> = {};
  protected poiStatusDrafts: Record<string, ModerationStatus> = {};

  ngOnInit(): void {
    this.loadPendingItems();
  }

  loadPendingItems(): void {
    this.loading = true;
    this.errorMessage = '';

    forkJoin({
      cities: this.cityService.getPendingCities().pipe(
        catchError(() => {
          this.errorMessage = 'Failed to load pending cities.';
          return of([] as City[]);
        }),
      ),
      pois: this.poiService.getPendingPois().pipe(
        catchError(() => {
          this.errorMessage = 'Failed to load pending POIs.';
          return of([] as poi[]);
        }),
      ),
    })
      .pipe(finalize(() => {
        this.loading = false;
      }))
      .subscribe({
      next: ({ cities, pois }) => {
        this.pendingCities = cities;
        this.pendingPois = pois;
        this.seedDraftStatuses();
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Failed to load pending moderation items.';
      },
    });
  }

  updateCityStatus(id: string, status: ModerationStatus): void {
    this.updateStatus('city', id, status);
  }

  updatePoiStatus(id: string, status: ModerationStatus): void {
    this.updateStatus('poi', id, status);
  }

  applyCityStatus(id: string): void {
    const status = this.cityStatusDrafts[id];
    if (status) {
      this.updateCityStatus(id, status);
    }
  }

  applyPoiStatus(id: string): void {
    const status = this.poiStatusDrafts[id];
    if (status) {
      this.updatePoiStatus(id, status);
    }
  }

  trackByCityId(_: number, city: City): string {
    return city.id ?? city.name;
  }

  trackByPoiId(_: number, item: poi): string {
    return item.id ?? item.name;
  }

  private seedDraftStatuses(): void {
    for (const city of this.pendingCities) {
      if (city.id && !this.cityStatusDrafts[city.id]) {
        this.cityStatusDrafts[city.id] = 'APPROVED';
      }
    }

    for (const item of this.pendingPois) {
      if (item.id && !this.poiStatusDrafts[item.id]) {
        this.poiStatusDrafts[item.id] = 'APPROVED';
      }
    }
  }

  private updateStatus(entity: ModerationEntity, id: string, status: ModerationStatus): void {
    this.moderationService.updateStatus(entity, id, status).subscribe({
      next: () => {
        this.loadPendingItems();
      },
      error: () => {
        this.errorMessage = `Unable to update status ${status.toLowerCase()} for ${entity}.`;
      },
    });
  }
}
