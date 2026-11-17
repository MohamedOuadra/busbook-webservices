import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ReservationApiService } from './reservation.service';
import { environment } from '../../../environments/environment';

describe('ReservationApiService', () => {
  let service: ReservationApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ReservationApiService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(ReservationApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should post createReservation', () => {
    const mockRequest = { tripId: 1, travelerId: 1, seatId: 5 };
    const mockResponse = {
      id: 10,
      tripId: 1,
      travelerId: 1,
      seatId: 5,
      status: 'EN_ATTENTE' as const,
      reservationDate: '2026-11-20T10:00:00',
      expiresAt: '2026-11-20T10:10:00',
      confirmedAt: null,
    };

    service.createReservation(mockRequest).subscribe((res) => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.reservationApiUrl}/reservations`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should check seat availability', () => {
    service.checkSeatAvailability(1, 5).subscribe((available) => {
      expect(available).toBe(true);
    });

    const req = httpMock.expectOne(
      `${environment.reservationApiUrl}/reservations/availability?tripId=1&seatId=5`
    );
    expect(req.request.method).toBe('GET');
    req.flush(true);
  });
});
