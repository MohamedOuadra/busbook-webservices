import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TicketViewComponent } from './ticket-view.component';

describe('TicketViewComponent', () => {
  let component: TicketViewComponent;
  let fixture: ComponentFixture<TicketViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketViewComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TicketViewComponent);
    component = fixture.componentInstance;
    component.ticket = {
      id: 1,
      ticketNumber: 'TKT-20261116-A1B2C3D4',
      reservationId: 10,
      tripId: 1,
      seatNumber: 14,
      travelerName: 'Anass El Rhattas',
      travelerEmail: 'anass@example.com',
      qrCode: 'eyJ0aWNrZXQiOiJUS1QtMjAyNjExMTYifQ==',
      issuedAt: '2026-11-16T18:00:00Z',
      valid: true,
    };
    component.trip = {
      id: 1,
      villeDepart: 'Agadir',
      villeArrivee: 'Casablanca',
      dateDepart: '2026-11-20T08:00:00',
      dateArrivee: '2026-11-20T14:00:00',
      prix: 120,
      bus: { id: 1, immatriculation: '12345-A-6', capacite: 40, type: 'Confort' },
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should format times and dates properly', () => {
    expect(component.formatTime('2026-11-20T08:00:00')).toBe('08:00');
    expect(component.formatDate('2026-11-20T08:00:00')).toBe('2026-11-20');
  });
});
