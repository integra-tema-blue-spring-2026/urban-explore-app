import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ReviewDto } from '../../models/reviewDto';

@Component({
  selector: 'app-review-list',
  templateUrl: './review-list.html',
  styleUrl: './review-list.css',
})
export class ReviewListComponent {
  @Input() reviews: ReviewDto[] = [];
  @Input() isLoading = false;
  @Input() isWriting = false;
  @Output() createReview = new EventEmitter<void>();
  @Output() cancelEdit = new EventEmitter<void>();
  @Output() editReview = new EventEmitter<ReviewDto>();
  @Output() deleteReview = new EventEmitter<ReviewDto>();

  onWriteButtonClick(): void {
    if (this.isWriting) {
      this.cancelEdit.emit();
    } else {
      this.createReview.emit();
    }
  }

  protected formatPostedDate(postedDate: string): string {
    const parsedDate = new Date(postedDate);
    return Number.isNaN(parsedDate.getTime()) ? postedDate : parsedDate.toLocaleString();
  }
}
