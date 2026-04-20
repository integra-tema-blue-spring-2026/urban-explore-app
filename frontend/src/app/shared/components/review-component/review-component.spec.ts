import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { ReviewComponent } from './review-component';
import { ReviewController } from '../../service/review-controller';

describe('ReviewComponent', () => {
  let component: ReviewComponent;
  let fixture: ComponentFixture<ReviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewComponent],
      providers: [
        {
          provide: ReviewController,
          useValue: {
            getReviews: () => of([]),
            addReview: () => of({}),
            updateReview: () => of({}),
            deleteReview: () => of(void 0),
          },
        },
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
