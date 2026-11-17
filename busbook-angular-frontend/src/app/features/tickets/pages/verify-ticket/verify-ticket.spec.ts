import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { VerifyTicketPage } from './verify-ticket';
import { TicketApiService } from '../../../../core/services/ticket.service';
import { TicketResponse } from '../../../../core/models/ticket.model';

describe('VerifyTicketPage', () => {
  let component: VerifyTicketPage;
  let fixture: ComponentFixture<VerifyTicketPage>;
  let ticketApiMock: any;

  const mockTicket: TicketResponse = {
    id: 1,
    ticketNumber: 'TCK-20261116-E81EB8F2',
    reservationId: 10,
    tripId: 1,
    seatNumber: 'A1',
    travelerName: 'Anas Test',
    travelerEmail: 'anas@test.com',
    qrCode: 'BUSBOOK|TCK-20261116-E81EB8F2|RES:10',
    issuedAt: '2026-11-16T20:00:00',
    valid: true,
  };

  beforeEach(async () => {
    ticketApiMock = {
      verifyTicket: vi.fn().mockReturnValue(of(mockTicket)),
    };

    await TestBed.configureTestingModule({
      imports: [VerifyTicketPage],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TicketApiService, useValue: ticketApiMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VerifyTicketPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should verify ticket successfully and update view state', () => {
    component.ticketNumber = 'TCK-20261116-E81EB8F2';
    component.onVerify();

    expect(ticketApiMock.verifyTicket).toHaveBeenCalledWith('TCK-20261116-E81EB8F2');
    expect(component.ticket).toEqual(mockTicket);
    expect(component.isLoading).toBe(false);
    expect(component.hasSearched).toBe(true);
    expect(component.errorMessage).toBeNull();
  });

  it('should handle verification error and set error message', () => {
    ticketApiMock.verifyTicket.mockReturnValue(
      throwError(() => ({ error: { message: 'Billet introuvable avec ce numéro.' } }))
    );

    component.ticketNumber = 'TCK-UNKNOWN';
    component.onVerify();

    expect(component.isLoading).toBe(false);
    expect(component.ticket).toBeUndefined();
    expect(component.hasSearched).toBe(true);
    expect(component.errorMessage).toBe('Billet introuvable avec ce numéro.');
  });
});
