import {Component, OnInit, ChangeDetectorRef, inject} from '@angular/core';
import { CityService } from '../../core/api/services/city.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CityDto } from '../../core/api/generated/model/cityDto';
import { CreateCityDto } from '../../core/api/generated/model/createCityDto';
import { UpdateCityDto } from '../../core/api/generated/model/updateCityDto';
import {ButtonDirective} from 'primeng/button';
import {Router} from '@angular/router';

@Component({
  selector: 'app-city',
  templateUrl: './city.html',
  styleUrl: './city.css',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, ButtonDirective],
})
export class CityComponent implements OnInit {
  cities: CityDto[] = [];
  editingId: string | null = null;

  private readonly router = inject(Router);

  cityForm = new FormGroup({
    name: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    country: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    description: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    population: new FormControl(1, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(1)],
    }),
    imageUrl: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    latitude: new FormControl(0, { nonNullable: true, validators: [Validators.required] }),
    longitude: new FormControl(0, { nonNullable: true, validators: [Validators.required] }),
  });

  constructor(
    private cityService: CityService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.loadCities();
  }

  loadCities() {
    this.cityService.getCities().subscribe((data) => {
      this.cities = data;
      this.cdr.markForCheck();
    });
  }

  startEdit(c: CityDto) {
    if (!c.id) {
      return;
    }
    this.editingId = c.id;
    this.cityForm.patchValue({
      name: c.name ?? '',
      country: c.country ?? '',
      description: c.description ?? '',
      population: c.population ?? 1,
      imageUrl: c.imageUrl ?? '',
      latitude: c.coordinates?.latitude ?? 0,
      longitude: c.coordinates?.longitude ?? 0,
    });
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
      const updateData: UpdateCityDto = {
        description: rawValue.description,
        imageUrl: rawValue.imageUrl,
        coordinates: {
          latitude: rawValue.latitude,
          longitude: rawValue.longitude,
        },
      };
      this.cityService.updateCity(this.editingId, updateData).subscribe(() => {
        alert('City updated successfully!');
        this.finalizeAction();
      });
    } else {
      const createData: CreateCityDto = {
        name: rawValue.name,
        country: rawValue.country,
        description: rawValue.description,
        population: rawValue.population,
        imageUrl: rawValue.imageUrl,
        coordinates: {
          latitude: rawValue.latitude,
          longitude: rawValue.longitude,
        },
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

  onShowOnMap(id: string | undefined) {
    if(!id) return;
    this.router.navigateByUrl('cities/' + id + '/map');
  }

  cancelEdit() {
    this.editingId = null;
    this.cityForm.reset({ population: 1, latitude: 0, longitude: 0 });
    this.enableAllFields();
  }
  private finalizeAction() {
    this.editingId = null;
    this.enableAllFields(); // Unlock
    this.loadCities();
    this.cityForm.reset({ population: 1, latitude: 0, longitude: 0 });
  }

  protected readonly CityStatus = CityDto.StatusEnum;
}
