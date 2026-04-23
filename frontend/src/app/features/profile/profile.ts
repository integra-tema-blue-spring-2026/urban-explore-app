import { Component, OnInit, inject } from '@angular/core';
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

  userId: string | null = null;
  userReviews: UserProfileReviewDto[] = [];
  imageErrors: { [key: string]: boolean } = {};

  ngOnInit(): void {
    this.userId = this.route.snapshot.paramMap.get('id');

    if (this.userId) {
      this.loadUserReviews(this.userId);
    }
  }

  loadUserReviews(userId: string): void {
    this.reviewService.getUserProfileReviews(userId).subscribe({
      next: (reviews) => {
        this.userReviews = reviews;
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
