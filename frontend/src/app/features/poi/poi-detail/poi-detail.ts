import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PoiService } from '../../../core/services/poi';
import { poi } from '../../../core/utils/Poi-interface';

@Component({
  selector: 'app-poi-detail',
  templateUrl: './poi-detail.html',
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class PoiDetail implements OnInit {
  poi: poi | null = null;
  editingField: string | null = null; 
  editingValue: string = '';            

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private poiService: PoiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.poiService.getPoiById(id).subscribe((data: poi) => {
        this.poi = data;
        this.cdr.detectChanges();
      });
    }
  }

  startEdit(field: string, currentValue: string) {
    this.editingField = field;
    this.editingValue = currentValue;
  }

  saveEdit() {
    if (!this.poi || !this.editingField) return;
    (this.poi as any)[this.editingField] = this.editingValue; 
    this.editingField = null;
    this.poiService.updatePoi(this.poi.id!, this.poi).subscribe(() => {
      this.cdr.detectChanges();
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
