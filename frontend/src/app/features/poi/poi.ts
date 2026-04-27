import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { PoiService } from '../../core/services/poi';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PointOfInterestRequestDto, PointOfInterestResponseDto } from '../../core/api/generated';

@Component({
  selector: 'app-poi',
  templateUrl: './poi.html',
  styleUrls: ['./poi.css'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
})
export class Poi implements OnInit {
  private poiService = inject(PoiService);
  private cdr = inject(ChangeDetectorRef);

  pois: PointOfInterestResponseDto[] = [];
  editingId: string | null = null;

  poiForm = new FormGroup({
    name: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    description: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    address: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    type: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    cityId: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    latitude: new FormControl(0, { nonNullable: true, validators: [Validators.required] }),
    longitude: new FormControl(0, { nonNullable: true, validators: [Validators.required] }),
  });

  ngOnInit(): void {
    this.loadPois();
  }

  startEdit(p: PointOfInterestResponseDto) {
    if (!p.id) {
      return;
    }
    this.editingId = p.id;
    this.poiForm.setValue({
      name: p.name ?? '',
      description: p.description ?? '',
      address: p.address ?? '',
      type: p.type ?? '',
      cityId: p.cityId ?? '',
      latitude: p.coordinates?.latitude ?? 0,
      longitude: p.coordinates?.longitude ?? 0,
    });
  }

  cancelEdit() {
    this.editingId = null;
    this.poiForm.reset();
  }

  loadPois() {
    this.poiService.getPois().subscribe({
      next: (data: PointOfInterestResponseDto[]) => {
        this.pois = data;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error loading POIs:', error);
        alert('Failed to load POIs');
      },
    });
  }

  savePoi() {
    if (!this.poiForm.valid) {
      this.poiForm.markAllAsTouched();
      return;
    }

    const rawValue = this.poiForm.getRawValue();
    const poiRequestDto: PointOfInterestRequestDto = {
      name: rawValue.name,
      description: rawValue.description,
      address: rawValue.address,
      type: rawValue.type as PointOfInterestRequestDto.TypeEnum,
      cityId: rawValue.cityId,
      coordinates: {
        latitude: rawValue.latitude,
        longitude: rawValue.longitude,
      },
    };

    if (this.editingId) {
      this.poiService.updatePoi(this.editingId, poiRequestDto).subscribe({
        next: () => {
          this.loadPois();
          this.editingId = null;
          this.poiForm.reset();
          alert('POI updated successfully!');
        },
        error: (error) => {
          console.error('Error updating POI:', error);
          alert('Failed to update POI');
        },
      });
    } else {
      this.poiService.addPoi(poiRequestDto).subscribe({
        next: () => {
          this.loadPois();
          this.poiForm.reset();
          alert('POI added successfully!');
        },
        error: (error) => {
          console.error('Error adding POI:', error);
          alert('Failed to add POI');
        },
      });
    }
  }

  onDelete(id: string | undefined) {
    if (!id) return;
    if (confirm('Are you sure you want to delete this POI?')) {
      this.poiService.deletePoi(id).subscribe({
        next: () => {
          this.loadPois();
          alert('POI deleted successfully!');
        },
        error: (error) => {
          console.error('Error deleting POI:', error);
          alert('Failed to delete POI');
        },
      });
    }
  }
}
