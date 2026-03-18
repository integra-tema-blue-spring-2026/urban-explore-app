import {Routes} from '@angular/router';
import {Home} from './features/home/home';
import {About} from './features/about/about';
import { Poi } from './features/poi/poi';
import { PoiDetail } from './features/poi/poi-detail/poi-detail';

export const routes: Routes = [
  {
    path: '',
    component: Home,
  },

  {
    path: 'about',
    component: About
  },

  {
    path: 'poi',
    component: Poi
  },

  {
    path: 'poi/details/:id',
    component: PoiDetail
  }

];
