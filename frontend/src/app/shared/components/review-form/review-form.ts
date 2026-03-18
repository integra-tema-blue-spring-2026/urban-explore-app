import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-review-form',
  imports: [ReactiveFormsModule],
  templateUrl: './review-form.html',
  styleUrl: './review-form.css',
})
export class ReviewFormComponent {
  @Input({ required: true }) form!: FormGroup;
  @Input() isWriting = false;
  @Input() isSubmitting = false;
  @Input() knownPoiIds: number[] = [];
  @Output() submitReview = new EventEmitter<void>();
  @Output() cancelEdit = new EventEmitter<void>();

  protected hasError(controlName: string, errorName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.touched && control.hasError(errorName);
  }
}