import { Component, OnInit, inject } from '@angular/core';
import { PoiService } from '../../core/services/poi';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { poi } from '../../core/utils/Poi-interface';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-poi',
  templateUrl: './poi.html',
  styleUrls: ['./poi.css'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink]
})
export class Poi implements OnInit {
  private poiService = inject(PoiService);

  pois: poi[] = [];
  editingId: string | null = null;

  poiForm = new FormGroup({
    name: new FormControl('', Validators.required),
    description: new FormControl('', Validators.required),
    address: new FormControl('', Validators.required),
    type: new FormControl('', Validators.required),
    cityId: new FormControl('', Validators.required)
  });

  ngOnInit(): void {
    this.loadPois();
  }

  startEdit(p: poi) {
    this.editingId = p.id!;
    this.poiForm.setValue({
      name: p.name,
      description: p.description,
      address: p.address,
      type: p.type,
      cityId: p.cityId
    });
  }

  cancelEdit() {
    this.editingId = null;
    this.poiForm.reset();
  }

  loadPois() {
    this.poiService.getPois().subscribe((data: poi[]) => {
      this.pois = data;
    });
  }

  savePoi() {
    if (!this.poiForm.valid) {
      this.poiForm.markAllAsTouched();
      return;
    }
    this.poiService.addPoi(this.poiForm.value as poi).subscribe(() => {
      this.loadPois();
      this.poiForm.reset();
      alert('POI added successfully!');
    });
  }

  onDelete(id: string | undefined) {
    if (!id) return;
    if (confirm('Are you sure you want to delete this POI?')) {
      this.poiService.deletePoi(id).subscribe(() => {
        this.loadPois();
        alert('POI deleted successfully!');
      });
    }
  }
}
