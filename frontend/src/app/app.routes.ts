import {Routes} from '@angular/router';
import {Home} from './features/home/home';
import {CityComponent} from './features/cityfrontend/city';
import {Auth} from './features/auth/auth';
import {Poi} from './features/poi/poi';
import {PoiDetail} from './features/poi/poi-detail/poi-detail';
import {authGuard} from './core/services/auth.guard';

export const routes: Routes = [
  {path: '', pathMatch:'full', component: Home},
  {path: 'auth', component: Auth},
  {path: 'cities', canActivate: [authGuard], component: CityComponent},
  {path: 'poi', canActivate: [authGuard], component: Poi},
  {path: 'poi/details/:id', canActivate: [authGuard], component: PoiDetail},
  {path: '**', redirectTo: ''},
];
