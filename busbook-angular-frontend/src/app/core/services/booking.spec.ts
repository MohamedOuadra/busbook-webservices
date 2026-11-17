import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { BookingService } from './booking';

describe('BookingService', () => {
  let service: BookingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        BookingService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(BookingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should update selected seat', () => {
    service.setSelectedSeat(14);
    expect(service.currentSeat).toBe(14);
  });

  it('should countdown and flag expired when diff <= 0', () => {
    // Expired in past
    const past = new Date(Date.now() - 5000).toISOString();
    service.startCountdown(past);
    expect(service.currentReservation).toBeNull();
  });
});
