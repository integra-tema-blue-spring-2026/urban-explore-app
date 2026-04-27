import { Component } from '@angular/core';
import { Button } from 'primeng/button';
import { RouterLink } from '@angular/router';
import { ActivityFeedComponent } from './activity-feed';

@Component({
  selector: 'app-home',
  imports: [Button, RouterLink, ActivityFeedComponent],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {}
