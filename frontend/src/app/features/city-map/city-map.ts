import {AfterViewInit, Component, inject, NgZone, OnDestroy, OnInit} from '@angular/core';
import {PointOfInterestResponseDto} from '../../core/api/generated';
import {ActivatedRoute, Router} from '@angular/router';
import {PoiService} from '../../core/services/poi';
import {CityService} from '../../core/api/services/city.service';
import * as L from 'leaflet';

@Component({
  selector: 'app-city-map',
  imports: [],
  templateUrl: './city-map.html',
  styleUrl: './city-map.css',
})
export class CityMap implements OnInit, AfterViewInit, OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly poiService = inject(PoiService);
  private readonly cityService = inject(CityService);
  private cityLatitude = 0;
  private cityLongitude = 0;
  private pois: PointOfInterestResponseDto[] = [];
  private map!: L.Map;
  private router = inject(Router);
  private zone: NgZone = inject(NgZone);
  private fixDefaultIcon(): void {
    L.Marker.prototype.options.icon = L.icon({
      iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
      iconUrl: 'assets/leaflet/marker-icon.png',
      shadowUrl: 'assets/leaflet/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      tooltipAnchor: [16, -28],
      shadowSize: [41, 41]
    });
  }

  ngOnInit(): void {
    this.fixDefaultIcon();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.cityService.getCityById(id).subscribe({
        next: (city) => {
          console.log(`Loaded city details: ${city.name} with coordinates [${city.coordinates?.latitude}, ${city.coordinates?.longitude}]`);
          this.cityLatitude = city.coordinates?.latitude ?? 0;
          this.cityLongitude = city.coordinates?.longitude ?? 0;
          this.initMap();
        },
        error: (error) => {
          console.error('Error loading city details:', error);
          alert('Failed to load city details');
        },
      });

      this.poiService.getPois({cityId: id}).subscribe({
        next: (pois: PointOfInterestResponseDto[]) => {
          this.pois = pois;
          if (this.map) {
            this.addMarkers();
          }
        },
        error: (error) => {
          console.error('Error loading POIs for city:', error);
          alert('Failed to load points of interest for the city');
        },
      });
    }
  }

  ngAfterViewInit(): void {
    if (this.cityLatitude !== 0 && this.cityLongitude !== 0) {
      this.initMap();
    }
    if (this.pois.length > 0) {
      this.addMarkers();
    }
  }

  private initMap(): void {
    console.log(`Initializing map at coordinates: [${this.cityLatitude}, ${this.cityLongitude}]`);
    this.map = L.map('map', {
      center: [this.cityLatitude, this.cityLongitude],
      zoom: 13
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);
  }

  private addMarkers(): void {
    this.pois.forEach(poi => {
      console.log(`Adding marker for POI: ${poi.name} at [${poi.coordinates?.latitude}, ${poi.coordinates?.longitude}]`);
      const marker = L.marker([poi.coordinates?.latitude ?? 0, poi.coordinates?.longitude ?? 0]).addTo(this.map)

      const container = document.createElement('div');
      const link = document.createElement('a');

      link.innerText = `View details for ${poi.name}`;
      link.href = `/poi/details/${poi.id}`;
      link.style.cssText = 'color: #007bff; text-decoration: underline; cursor: pointer;';

      link.addEventListener('click', (e: MouseEvent) => {
        e.preventDefault();
        this.zone.run(() => {
          this.router.navigate(['/poi/details', poi.id]);
        });
      });

      container.innerHTML = `<b>${poi.name}</b><br><b>${poi.type}</b><br>`;
      container.appendChild(link);

      marker.bindPopup(container);
    })
  }

  ngOnDestroy(): void {
    if (this.map) this.map.remove();
  }
}
