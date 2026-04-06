import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Poi } from './poi';

describe('Poi', () => {
  let component: Poi;
  let fixture: ComponentFixture<Poi>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Poi]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Poi);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
