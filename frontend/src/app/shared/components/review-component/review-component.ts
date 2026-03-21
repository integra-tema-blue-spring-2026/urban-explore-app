import { ChangeDetectorRef, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ReviewFormComponent } from '../review-form/review-form';
import { ReviewListComponent } from '../review-list/review-list';
import { ReviewCreateDto } from '../../models/reviewCreateDto';
import { ReviewDto } from '../../models/reviewDto';
import { ReviewUpdateDto } from '../../models/reviewUpdateDto';
import { ReviewController } from '../../service/review-controller';

@Component({
  selector: 'app-review-component',
  imports: [ReviewFormComponent, ReviewListComponent],
  templateUrl: './review-component.html',
  styleUrl: './review-component.css',
})
export class ReviewComponent {
  @Input() poiId: string | null = null;
  @Input() userId: string | null = null;

  reviews: ReviewDto[] = [];
  selectedReview: ReviewDto | null = null;
  showForm = false;
  isLoading = false;
  isSubmitting = false;

  readonly reviewForm = new FormGroup({
    text: new FormControl('', [Validators.required, Validators.maxLength(1000)]),
    rating: new FormControl<number | null>(null, [Validators.required, Validators.min(1), Validators.max(5)]),
    userId: new FormControl<number | null>(null, [Validators.required, Validators.min(1)]),
    poiId: new FormControl<number | null>(null, [Validators.required, Validators.min(1)]),
  });

  constructor(
    private reviewController: ReviewController,
    private cdr: ChangeDetectorRef,
  ) {
  }

  ngOnInit() {
    this.applyParentPoiIdToForm();
    this.applyCurrentUserIdToForm();
    this.loadReviews();
  }

  private parseParentPoiId(): number | null {
    if (this.poiId === null) {
      return null;
    }

    const parsedPoiId = Number(this.poiId);
    return Number.isInteger(parsedPoiId) && parsedPoiId > 0 ? parsedPoiId : null;
  }

  private applyParentPoiIdToForm(): void {
    const parentPoiId = this.parseParentPoiId();

    if (parentPoiId === null) {
      this.reviewForm.controls.poiId.enable();
      return;
    }

    this.reviewForm.patchValue({ poiId: parentPoiId });
    this.reviewForm.controls.poiId.enable();
  }

  private applyCurrentUserIdToForm(): void {
    if (this.userId === null) {
      this.reviewForm.controls.userId.enable();
      return;
    }

    this.reviewForm.patchValue({ userId: Number(this.userId) });
    this.reviewForm.controls.userId.disable();
  }
  loadReviews(): void {
    this.isLoading = true;
    this.reviewController.getReviews().subscribe({
      next: (reviews) => {
        this.reviews = reviews;
        this.isLoading = false;
        setTimeout(() => this.cdr.detectChanges());
      },
      error: () => {
        this.isLoading = false;
        setTimeout(() => this.cdr.detectChanges());
        window.alert('Failed to load reviews from the backend.');
      },
    });
  }

  submitReview(): void {
    if (this.reviewForm.invalid) {
      this.reviewForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;

    if (this.selectedReview) {
      const updatePayload: ReviewUpdateDto = {
        text: this.reviewForm.controls.text.value?.trim() ?? '',
        rating: Number(this.reviewForm.controls.rating.value),
      };

      this.reviewController.updateReview(this.selectedReview.id, updatePayload).subscribe({
        next: () => {
          window.alert('Review updated successfully.');
          setTimeout(() => this.cancelEdit(), 0);
          this.isSubmitting = false;
          this.loadReviews();
        },
        error: () => {
          this.isSubmitting = false;
          window.alert('Updating the review failed.');
        },
      });

      return;
    }

    const createPayload: ReviewCreateDto = {
      text: this.reviewForm.controls.text.value?.trim() ?? '',
      rating: Number(this.reviewForm.controls.rating.value),
      userId: Number(this.reviewForm.controls.userId.value),
      poiId: Number(this.reviewForm.controls.poiId.value),
    };

    this.reviewController.addReview(createPayload).subscribe({
      next: () => {
        window.alert('Review created successfully.');
        setTimeout(() => this.cancelEdit(), 0);
        this.isSubmitting = false;
        this.loadReviews();
      },
      error: () => {
        this.isSubmitting = false;
        window.alert('Creating the review failed.');
      },
    });
  }

  startEdit(review: ReviewDto): void {
    this.selectedReview = review;
    this.showForm = true;
    this.reviewForm.patchValue({
      text: review.text,
      rating: review.rating,
      userId: review.userId,
      poiId: review.poiId,
    });
    this.reviewForm.controls.userId.disable();
    this.reviewForm.controls.poiId.disable();
    this.reviewForm.markAsPristine();
  }

  cancelEdit(): void {
    this.selectedReview = null;
    this.showForm = false;
    this.reviewForm.reset();

    this.applyCurrentUserIdToForm();
    this.applyParentPoiIdToForm();
    
    this.reviewForm.markAsPristine();
  }

  openCreateForm(): void {
    if (this.showForm) {
      this.cancelEdit();
      return;
    }

    this.selectedReview = null;
    this.showForm = true;

    this.reviewForm.reset();

    this.applyParentPoiIdToForm();
    this.applyCurrentUserIdToForm();
    
    this.reviewForm.markAsPristine();
  }

  deleteReview(review: ReviewDto): void {
    if (!window.confirm('Delete this review?')) {
      return;
    }

    this.reviewController.deleteReview(review.id).subscribe({
      next: () => {
        this.cancelEdit();

        window.alert('Review deleted successfully.');
        this.loadReviews();
      },
      error: () => {
        window.alert('Deleting the review failed.');
      },
    });
  }

}
