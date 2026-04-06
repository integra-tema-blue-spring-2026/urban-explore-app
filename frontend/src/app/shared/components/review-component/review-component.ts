import { ChangeDetectorRef, Component, Input, inject } from '@angular/core';
import { FormControl, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ReviewFormComponent } from '../review-form/review-form';
import { ReviewListComponent } from '../review-list/review-list';
import { ReviewCreateDto } from '../../models/reviewCreateDto';
import { ReviewDto } from '../../models/reviewDto';
import { ReviewUpdateDto } from '../../models/reviewUpdateDto';
import { ReviewController } from '../../service/review-controller';

@Component({
  selector: 'app-review-component',
  imports: [ReviewFormComponent, ReviewListComponent, ConfirmDialogModule, ToastModule],
  providers: [MessageService, ConfirmationService],
  templateUrl: './review-component.html',
  styleUrl: './review-component.css',
})
export class ReviewComponent {
  @Input() poiId: string | null | undefined = null;
  @Input() userId: string | null | undefined = null;

  reviews: ReviewDto[] = [];
  selectedReview: ReviewDto | null = null;
  showForm = false;
  isLoading = false;
  isSubmitting = false;

  private readonly uuidPattern = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

  private uuidValidator = (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return { required: true };
    return this.uuidPattern.test(control.value) ? null : { invalidUuid: true };
  };

  readonly reviewForm = new FormGroup({
    text: new FormControl('', [Validators.required, Validators.maxLength(1000)]),
    rating: new FormControl<number | null>(null, [Validators.required, Validators.min(1), Validators.max(5)]),
    userId: new FormControl<string | null>(null, [Validators.required, this.uuidValidator]),
    poiId: new FormControl<string | null>(null, [Validators.required, this.uuidValidator]),
  });

  private reviewController = inject(ReviewController);
  private cdr = inject(ChangeDetectorRef);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);

  ngOnInit() {
    this.applyParentPoiIdToForm();
    this.applyCurrentUserIdToForm();
    this.loadReviews();
  }

  private getErrorMessage(error: any): string {
    if (!error) return 'An unknown error occurred.';
    
    // HTTP error with detailed message
    if (error.error?.message) return error.error.message;
    if (error.error?.detail) return error.error.detail;
    if (error.error?.error) return error.error.error;
    
    // Error message from error object
    if (error.message) return error.message;
    
    // Status code information
    if (error.status) return `HTTP Error ${error.status}: ${error.statusText || 'Request failed'}`;
    
    return 'An unknown error occurred.';
  }

  private isValidUuid(value: string | null | undefined): boolean {
    if (!value) return false;
    return this.uuidPattern.test(value);
  }

  private applyParentPoiIdToForm(): void {
    if (this.isValidUuid(this.poiId)) {
      this.reviewForm.patchValue({ poiId: this.poiId });
      this.reviewForm.controls.poiId.disable();
    } else {
      this.reviewForm.controls.poiId.enable();
    }
  }

  private applyCurrentUserIdToForm(): void {
    if (this.isValidUuid(this.userId)) {
      this.reviewForm.patchValue({ userId: this.userId });
      this.reviewForm.controls.userId.disable();
    } else {
      this.reviewForm.controls.userId.enable();
    }
  }
  async loadReviews(): Promise<void> {
    this.isLoading = true;
    this.cdr.detectChanges();

    try {
      const poiId = this.poiId;
      const userId = this.userId;
      this.reviews = await firstValueFrom(this.reviewController.getFilteredReviews(poiId, userId));
    } catch (error) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: `Failed to load reviews. ${this.getErrorMessage(error)}`,
        sticky: true,
        closable: true,
        life: 0
      });
    } finally {
      this.isLoading = false;
      this.cdr.detectChanges();
    }
  }

  async submitReview(): Promise<void> {
    if (this.reviewForm.invalid) {
      this.reviewForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.cdr.detectChanges();

    try {
      if (this.selectedReview) {
        const updatePayload: ReviewUpdateDto = {
          text: this.reviewForm.controls.text.value?.trim() ?? '',
          rating: Number(this.reviewForm.controls.rating.value),
        };

        await firstValueFrom(this.reviewController.updateReview(this.selectedReview.id, updatePayload));
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Review updated successfully.',
          sticky: false,
          closable: true,
          life: 5000
        });
        this.cancelEdit();
        await this.loadReviews();
        return;
      }

      const createPayload: ReviewCreateDto = {
        text: this.reviewForm.controls.text.value?.trim() ?? '',
        rating: Number(this.reviewForm.controls.rating.value),
        userId: (this.reviewForm.controls.userId.value as string | null) ?? '',
        poiId: (this.reviewForm.controls.poiId.value as string | null) ?? '',
      };

      await firstValueFrom(this.reviewController.addReview(createPayload));
      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Review created successfully.',
        sticky: false,
        closable: true,
        life: 5000
      });
      this.cancelEdit();
      await this.loadReviews();
    } catch (error) {
      const action = this.selectedReview ? 'updating' : 'creating';
      const baseMessage = `Failed to ${action} review.`;
      const detailedMessage = `${baseMessage} ${this.getErrorMessage(error)}`;
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: detailedMessage,
        sticky: true,
        closable: true,
        life: 0
      });
    } finally {
      this.isSubmitting = false;
      this.cdr.detectChanges();
    }
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

  async deleteReview(review: ReviewDto): Promise<void> {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete this review?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: async () => {
        try {
          console.log('Deleting review with ID:', review.id);
          await firstValueFrom(this.reviewController.deleteReview(review.id));
          console.log('Review deleted successfully');
          this.cancelEdit();
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'Review deleted successfully.',
            sticky: false,
            closable: true,
            life: 5000
          });
          console.log('Reloading reviews...');
          await this.loadReviews();
          console.log('Reviews reloaded');
        } catch (error) {
          console.error('Delete error:', error);
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: `Failed to delete review. ${this.getErrorMessage(error)}`,
            sticky: true,
            closable: true,
            life: 0
          });
        }
      }
    });
  }
}
