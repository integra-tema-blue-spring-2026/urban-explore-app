import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PoiService } from '../../../core/services/poi';
import { PointOfInterestRequestDto } from '../../../core/api/generated/model/pointOfInterestRequestDto';
import { PointOfInterestResponseDto } from '../../../core/api/generated/model/pointOfInterestResponseDto';
import { ReviewComponent } from '../../../shared/components/review-component/review-component';

@Component({
  selector: 'app-poi-detail',
  templateUrl: './poi-detail.html',
  styleUrls: ['./poi-detail.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReviewComponent],
})
export class PoiDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private poiService = inject(PoiService);
  private cdr = inject(ChangeDetectorRef);

  poi: PointOfInterestResponseDto | null = null;
  editingField: string | null = null;
  editingValue = '';

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.poiService.getPoiById(id).subscribe({
        next: (data: PointOfInterestResponseDto) => {
          this.poi = data;
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error loading POI details:', error);
          alert('Failed to load POI details');
        },
      });
    }
  }

  startEdit(field: string, currentValue?: string) {
    this.editingField = field;
    this.editingValue = currentValue ?? '';
  }

  saveEdit() {
    if (!this.poi || !this.editingField) return;
    const poiId = this.poi.id;
    if (!poiId) return;

    const updatedPoi: PointOfInterestResponseDto = {
      ...this.poi,
      [this.editingField]: this.editingValue,
    };

    const requestDto: PointOfInterestRequestDto = {
      name: updatedPoi.name ?? '',
      description: updatedPoi.description ?? '',
      address: updatedPoi.address ?? '',
      type: (updatedPoi.type ?? PointOfInterestRequestDto.TypeEnum.Museum) as PointOfInterestRequestDto.TypeEnum,
      cityId: updatedPoi.cityId ?? '',
      coordinates: {
        latitude: updatedPoi.coordinates?.latitude ?? 0,
        longitude: updatedPoi.coordinates?.longitude ?? 0,
      },
    };

    this.poi = updatedPoi;
    this.editingField = null;
    this.poiService.updatePoi(poiId, requestDto).subscribe({
      next: () => {
        alert('POI updated successfully!');
      },
      error: (error) => {
        console.error('Error updating POI:', error);
        alert('Failed to update POI');
      },
    });
  }

  cancelEdit() {
    this.editingField = null;
    this.editingValue = '';
  }

  goBack() {
    this.router.navigate(['/poi']);
  }
}
