import { Routes } from '@angular/router';
import { Home } from './features/home/home';
import { CityComponent } from './features/cityfrontend/city'; // Import your new component

export const routes: Routes = [
  { path: '',
    component: Home },
  { path: 'cities',
    component: CityComponent }
];
