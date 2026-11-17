import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TicketApiService } from './ticket.service';
import { environment } from '../../../environments/environment';

describe('TicketApiService', () => {
  let service: TicketApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TicketApiService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(TicketApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create ticket', () => {
    const mockRequest = {
      reservationId: 10,
      tripId: 1,
      seatNumber: 5,
      travelerName: 'Anass El Rhattas',
      travelerEmail: 'anass@example.com',
    };
    const mockResponse = {
      id: 1,
      ticketNumber: 'TKT-20261120-ABCD1234',
      reservationId: 10,
      tripId: 1,
      seatNumber: 5,
      travelerName: 'Anass El Rhattas',
      travelerEmail: 'anass@example.com',
      qrCode: 'BASE64-QR-DATA',
      issuedAt: '2026-11-20T10:02:00',
      valid: true,
    };

    service.createTicket(mockRequest).subscribe((res) => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.ticketApiUrl}/tickets`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });
});
