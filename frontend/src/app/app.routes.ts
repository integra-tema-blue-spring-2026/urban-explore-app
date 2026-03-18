import {Routes} from '@angular/router';
import {Home} from './features/home/home';
import { ReviewComponent } from './shared/components/review-component/review-component';

export const routes: Routes = [
  {
    path: '',
    component: Home,
  },
  {
    path: 'poi',
    component: ReviewComponent,
  }
];
