import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CityService } from '../../core/api/services/city.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { City} from '../../shared/models/city.model';


@Component({
  selector: 'app-city',
  templateUrl: './city.html',
  styleUrl: './city.css',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule]
})
export class CityComponent implements    OnInit {
  cities: City[] = [];
  editingId: string | null = null;

  cityForm = new FormGroup({
    name: new FormControl('', Validators.required),
    country: new FormControl('', Validators.required),
    description: new FormControl('', Validators.required),
    population: new FormControl(1, [Validators.required, Validators.min(1)]),
    imageUrl: new FormControl('',Validators.required),

  });

  constructor(private cityService: CityService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void { this.loadCities(); }

  loadCities() {
    this.cityService.getCities().subscribe((data) => {
      this.cities = data;
      this.cdr.detectChanges();
    });
  }

  startEdit(c: City) {
    this.editingId = c.id!;
    this.cityForm.patchValue(c);
    this.cityForm.get('name')?.disable();
    this.cityForm.get('country')?.disable();
    this.cityForm.get('population')?.disable();
  }

  private enableAllFields() {
    this.cityForm.get('name')?.enable();
    this.cityForm.get('country')?.enable();
    this.cityForm.get('population')?.enable();
  }

  saveCity() {
    if (!this.cityForm.valid) return;
    const cityData = this.cityForm.getRawValue() as City;

    if (this.editingId) {
      this.cityService.updateCity(this.editingId, cityData).subscribe(() => {
        alert('City updated successfully!');
        this.finalizeAction();
      });
    } else {
      this.cityService.addCity(cityData).subscribe(() => {
        alert('City added successfully!');
        this.finalizeAction();
      });
    }
  }

  onDelete(id: string | undefined) {
    if (!id || !confirm('Please confirm you want to delete this')) return;
    this.cityService.deleteCity(id).subscribe(() => this.loadCities());
  }

  cancelEdit() {
    this.editingId = null;
    this.cityForm.reset({ population: 1 });
    this.enableAllFields();
  }
  private finalizeAction() {
    this.editingId = null;
    this.enableAllFields(); // Unlock
    this.loadCities();
    this.cityForm.reset({ population: 1 });
  }
}
