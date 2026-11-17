import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { TicketPage } from './ticket';
import { TicketApiService } from '../../../../core/services/ticket.service';
import { TripService } from '../../../../core/services/trip';
import { TicketResponse } from '../../../../core/models/ticket.model';

describe('TicketPage', () => {
  let component: TicketPage;
  let fixture: ComponentFixture<TicketPage>;
  let ticketApiMock: any;
  let tripServiceMock: any;

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
    tripServiceMock = {
      getTripById: vi.fn().mockReturnValue(
        of({
          id: 1,
          villeDepart: 'Agadir',
          villeArrivee: 'Casablanca',
          dateDepart: '2026-11-20T08:00:00',
          dateArrivee: '2026-11-20T14:00:00',
          prix: 120,
          bus: { id: 1, immatriculation: '12345-A-6', capacite: 40, type: 'Confort' },
        })
      ),
    };

    await TestBed.configureTestingModule({
      imports: [TicketPage],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TicketApiService, useValue: ticketApiMock },
        { provide: TripService, useValue: tripServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(new Map([['ticketNumber', 'TCK-20261116-E81EB8F2']])),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TicketPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load ticket details properly', () => {
    expect(component).toBeTruthy();
    expect(component.ticketNumber).toBe('TCK-20261116-E81EB8F2');
    expect(component.ticket).toEqual(mockTicket);
    expect(component.trip).toBeDefined();
    expect(component.isLoading).toBe(false);
  });

  it('should handle ticket not found error gracefully', () => {
    ticketApiMock.verifyTicket.mockReturnValue(
      throwError(() => ({ error: { message: 'Billet introuvable avec ce numéro.' } }))
    );

    component.ticketNumber = 'TCK-INVALID';
    component.loadTicket();

    expect(component.isLoading).toBe(false);
    expect(component.ticket).toBeUndefined();
    expect(component.errorMessage).toBe('Billet introuvable avec ce numéro.');
  });
});
