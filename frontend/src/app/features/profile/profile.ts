import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReviewController } from '../../shared/service/review-controller';
import { UserProfileReviewDto } from '../../shared/models/user/user-profile-review.dto';
import { UserService } from './user.service';
import { User } from '../../shared/models/user/user';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private reviewService = inject(ReviewController);
  private userService = inject(UserService);
  private cdr = inject(ChangeDetectorRef);

  profileUser: User | null = null;
  currentUser: User | null = null;
  userReviews: UserProfileReviewDto[] = [];
  imageErrors: { [key: string]: boolean } = {};
  followerCount = 0;
  followingCount = 0;
  isFollowing = false;
  isOwnProfile = false;
  userNotFound = false;


  private followersList: User[] = [];

  ngOnInit(): void {
    const username = this.route.snapshot.paramMap.get('id');
    if (!username) {
      this.router.navigate(['/home']);
      return;
    }

    const currentUsername = this.getCurrentUsername();
    this.isOwnProfile = currentUsername === username;


    this.reviewService.getUserProfileReviews(username).subscribe({
      next: reviews => {
        this.userReviews = reviews;
        this.cdr.detectChanges();
      },
      error: err => console.error('Could not load reviews:', err),
    });

    if (!this.isOwnProfile && currentUsername) {
      this.userService.getUserByUsername(currentUsername).subscribe({
        next: currentUser => {
          this.currentUser = currentUser;
          this.computeIsFollowing();
          this.cdr.detectChanges();
        },
        error: err => console.error('Could not load current user:', err),
      });
    }



    this.userService.getUserByUsername(username).subscribe({
      next: profileUser => {
        this.profileUser = profileUser;
        this.cdr.detectChanges();

        this.userService.getUserFollowers(profileUser.id).subscribe({
          next: followers => {
            this.followersList = followers;
            this.followerCount = followers.length;
            this.computeIsFollowing();
            this.cdr.detectChanges();

            this.userService.getUserFollowing(profileUser.id).subscribe({
              next: following => {
                this.followingCount = following.length;
                this.cdr.detectChanges();
              },
              error: err => console.error('Could not load following:', err),
            });
          },
          error: err => console.error('Could not load followers:', err),
        });
      },
      error: err => {
        if (err.status === 404) {
          this.userNotFound = true;
          this.cdr.detectChanges();
        } else {
          console.error('Could not load profile user:', err);
        }
      },
    });


  }

  private computeIsFollowing(): void {
    if (this.currentUser) {
      this.isFollowing = this.followersList.some(f => f.id === this.currentUser!.id);
    }
  }

  private getCurrentUsername(): string | null {
    const token = localStorage.getItem('token');
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
      return payload.sub ?? null;
    } catch {
      return null;
    }
  }

  handleAvatarError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = '/images/London.png'; 
  }
  toggleFollow(): void {
    if (!this.profileUser || !this.currentUser) return;

    const action$ = this.isFollowing
      ? this.userService.unfollowUser(this.profileUser.id, this.currentUser.id)
      : this.userService.followUser(this.profileUser.id, this.currentUser.id);

    action$.subscribe({
      next: () => {
        this.isFollowing = !this.isFollowing;
        this.followerCount += this.isFollowing ? 1 : -1;
        this.cdr.detectChanges();
      },
      error: err => console.error('Follow action failed:', err),
    });
  }

  handleImageError(reviewId: string): void {
    this.imageErrors[reviewId] = true;
  }
}
