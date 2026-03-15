import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { PoiService } from '../../core/services/poi';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { poi } from '../../core/utils/Poi-interface';

@Component({
  selector: 'app-poi',
  templateUrl: './poi.html',
  standalone: true, 
  imports: [ReactiveFormsModule, CommonModule] 
})
export class Poi implements OnInit {
  pois: poi[] = [];
  editingId: string | null = null;

  poiForm = new FormGroup({
    name: new FormControl('', Validators.required),
    description: new FormControl('', Validators.required),
    address: new FormControl('', Validators.required),
    type: new FormControl('', Validators.required),
    cityId: new FormControl('', Validators.required)
  });

  constructor(
    private poiService: PoiService,
    private cdr: ChangeDetectorRef //to detect changes after operations
  ) {}

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
      this.cdr.detectChanges();
    });
  }

  savePoi() {
    if (!this.poiForm.valid) return;
    if (this.editingId) {
      this.poiService.updatePoi(this.editingId, this.poiForm.value as poi).subscribe(() => {
        this.editingId = null;
        this.loadPois();
        this.poiForm.reset();
      });
    } else {
      this.poiService.addPoi(this.poiForm.value as poi).subscribe(() => {
        this.loadPois();
        this.poiForm.reset();
      });
    }
  }

  onDelete(id: string | undefined) {1
    if (!id) return;
    if (confirm('Are you sure?')) {
      this.poiService.deletePoi(id).subscribe(() => this.loadPois());
  }
 }
}
