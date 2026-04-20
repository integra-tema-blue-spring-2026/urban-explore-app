import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { ReviewController } from './review-controller';

describe('ReviewController', () => {
  let service: ReviewController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ReviewController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
