import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BookingPanel } from './booking-panel';

describe('BookingPanel', () => {
  let component: BookingPanel;
  let fixture: ComponentFixture<BookingPanel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookingPanel],
    }).compileComponents();

    fixture = TestBed.createComponent(BookingPanel);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
