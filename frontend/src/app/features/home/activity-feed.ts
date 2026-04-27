import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeedService } from '../../core/services/feed.service';
import { ActivityDto } from '../../shared/models/activity/Activity';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CardModule } from 'primeng/card';
import { SkeletonModule } from 'primeng/skeleton';
import { ScrollerModule } from 'primeng/scroller';
import { MessageModule } from 'primeng/message';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-activity-feed',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    SkeletonModule,
    ScrollerModule,
    MessageModule
  ],
  templateUrl: './activity-feed.html',
  styleUrl: './activity-feed.css'
})
export class ActivityFeedComponent implements OnInit, OnDestroy {
  activities: ActivityDto[] = [];
  isLoading = true;
  error: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    private feedService: FeedService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadFeed();
   
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.loadFeed();
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadFeed() {
    this.isLoading = true;
    this.error = null;

    console.log('Loading feed...');
    this.feedService.getFeed()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          console.log('Feed loaded successfully:', data);
          this.activities = data;
          this.isLoading = false;
          console.log('Activities count:', this.activities.length);
          console.log('isLoading is now:', this.isLoading);
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error loading feed:', err);
          if (err.status === 401) {
            this.error = 'Please log in to see the activity feed';
          } else {
            this.error = 'Failed to load activity feed';
          }
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
  }

  getActivityIcon(activityType: string): string {
    switch (activityType) {
      case 'REVIEW_CREATED':
        return 'pi pi-star';
      case 'POI_CREATED':
        return 'pi pi-map-marker';
      case 'QUEST_COMPLETED':
        return 'pi pi-check-circle';
      default:
        return 'pi pi-circle';
    }
  }

  getActivityColor(activityType: string): string {
    switch (activityType) {
      case 'REVIEW_CREATED':
        return '#f59e0b';
      case 'POI_CREATED':
        return '#8b5cf6';
      case 'QUEST_COMPLETED':
        return '#10b981';
      default:
        return '#6b7280';
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getActivityText(activity: ActivityDto): string {
    return `${activity.username} ${activity.actionDescription} at ${activity.targetName}`;
  }
}
