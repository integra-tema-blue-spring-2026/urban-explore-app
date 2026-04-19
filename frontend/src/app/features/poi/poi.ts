import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { PoiService } from '../../core/services/poi';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { poi } from '../../core/utils/Poi-interface';
import { RouterLink, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-poi',
  templateUrl: './poi.html',
  styleUrls: ['./poi.css'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink]
})
export class Poi implements OnInit {
  private poiService = inject(PoiService);
  private cdr = inject(ChangeDetectorRef);
  private route = inject(ActivatedRoute);

  pois: poi[] = [];
  editingId: string | null = null;
  cityId: string | null = null;

  poiForm = new FormGroup({
    name: new FormControl('', Validators.required),
    description: new FormControl('', Validators.required),
    address: new FormControl('', Validators.required),
    type: new FormControl('', Validators.required)
  });

  ngOnInit(): void {
    this.cityId = this.route.snapshot.paramMap.get('cityId');
    this.loadPois();
  }

  startEdit(p: poi) {
    this.editingId = p.id!;
    this.poiForm.setValue({
      name: p.name,
      description: p.description,
      address: p.address,
      type: p.type
    });
  }

  cancelEdit() {
    this.editingId = null;
    this.poiForm.reset();
  }

  loadPois() {
    this.poiService.getPois().subscribe({
      next: (data: poi[]) => {
        if (this.cityId) {
          this.pois = data.filter(p => p.cityId === this.cityId);
        } else {
          this.pois = data;
        }
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error loading POIs:', error);
        alert('Failed to load POIs');
      }
    });
  }

  savePoi() {
    if (!this.poiForm.valid) {
      this.poiForm.markAllAsTouched();
      return;
    }

    const poiData: poi = {
      ...(this.poiForm.value as any),
      cityId: this.cityId
    };

    if (this.editingId) {
      this.poiService.updatePoi(this.editingId, poiData).subscribe({
        next: () => {
          this.loadPois();
          this.editingId = null;
          this.poiForm.reset();
          alert('POI updated successfully!');
        },
        error: (error) => {
          console.error('Error updating POI:', error);
          alert('Failed to update POI');
        }
      });
    } else {
      this.poiService.addPoi(poiData).subscribe({
        next: () => {
          this.loadPois();
          this.poiForm.reset();
          alert('POI added successfully!');
        },
        error: (error) => {
          console.error('Error adding POI:', error);
          alert('Failed to add POI');
        }
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
        }
      });
    }
  }
}
