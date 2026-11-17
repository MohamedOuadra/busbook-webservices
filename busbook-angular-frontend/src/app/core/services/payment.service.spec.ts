import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { PaymentApiService } from './payment.service';
import { environment } from '../../../environments/environment';

describe('PaymentApiService', () => {
  let service: PaymentApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PaymentApiService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(PaymentApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should process payment', () => {
    const mockRequest = {
      reservationId: 10,
      amount: 120,
      paymentMethod: 'CARTE_BANCAIRE' as const,
    };
    const mockResponse = {
      id: 1,
      reservationId: 10,
      amount: 120,
      paymentMethod: 'CARTE_BANCAIRE' as const,
      status: 'SUCCESS' as const,
      transactionId: 'TX-123456',
      createdAt: '2026-11-20T10:01:00',
      completedAt: '2026-11-20T10:01:05',
    };

    service.processPayment(mockRequest).subscribe((res) => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.paymentApiUrl}/payments`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });
});
