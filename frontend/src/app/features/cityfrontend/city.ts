import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CityService } from '../../core/api/services/city.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  City,
  CityStatus,
  CreateCityRequest,
  UpdateCityRequest,
} from '../../shared/models/city.model';
import { TokenService } from '../../core/services/token.service';

@Component({
  selector: 'app-city',
  templateUrl: './city.html',
  styleUrl: './city.css',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
})
export class CityComponent implements OnInit {
  cities: City[] = [];
  editingId: string | null = null;
  isAdminUser = false;

  cityForm = new FormGroup({
    name: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    country: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    description: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    population: new FormControl(1, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(1)],
    }),
    imageUrl: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  constructor(
    private cityService: CityService,
    private cdr: ChangeDetectorRef,
    private tokenService: TokenService,
  ) {}

  ngOnInit(): void {
    this.isAdminUser = this.tokenService.isAdmin();
    this.loadCities();
  }

  loadCities() {
    this.cityService.getCitiesByStatus("APPROVED").subscribe((data) => {
      this.cities = data;
      this.cdr.markForCheck();
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
    const rawValue = this.cityForm.getRawValue();

    if (this.editingId) {
      const updateData: UpdateCityRequest = {
        description: rawValue.description,
        imageUrl: rawValue.imageUrl,
      };
      this.cityService.updateCity(this.editingId, updateData).subscribe(() => {
        alert('City updated successfully!');
        this.finalizeAction();
      });
    } else {
      const createData: CreateCityRequest = {
        name: rawValue.name,
        country: rawValue.country,
        description: rawValue.description,
        population: rawValue.population,
        imageUrl: rawValue.imageUrl,
      };
      this.cityService.addCity(createData).subscribe(() => {
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

  protected readonly CityStatus = CityStatus;
}
