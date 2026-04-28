import {Component, OnInit, inject, ChangeDetectorRef} from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ReviewController } from '../../shared/service/review-controller';
import { UserProfileReviewDto } from '../../shared/models/user/user-profile-review.dto';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [RouterLink], // Needed for [routerLink] in your HTML
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  private route = inject(ActivatedRoute);
  private reviewService = inject(ReviewController);
  private cdr = inject(ChangeDetectorRef);

  user: string | null = null;
  userReviews: UserProfileReviewDto[] = [];
  imageErrors: { [key: string]: boolean } = {};

  ngOnInit(): void {
    this.user = this.route.snapshot.paramMap.get('id');


    if (this.user) {
      this.loadUserReviews(this.user);
    }
  }

  loadUserReviews(user: string): void {
    this.reviewService.getUserProfileReviews(user).subscribe({
      next: (reviews) => {
        this.userReviews = reviews;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Could not load reviews:', err);
      }
    });
  }


  handleImageError(reviewId: string): void {
    this.imageErrors[reviewId] = true;
  }
}
