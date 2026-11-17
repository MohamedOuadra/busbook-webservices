import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PaymentSectionComponent } from './payment-section.component';

describe('PaymentSectionComponent', () => {
  let component: PaymentSectionComponent;
  let fixture: ComponentFixture<PaymentSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentSectionComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PaymentSectionComponent);
    component = fixture.componentInstance;
    component.trip = {
      id: 1,
      villeDepart: 'Agadir',
      villeArrivee: 'Casablanca',
      dateDepart: '2026-11-20T08:00:00',
      dateArrivee: '2026-11-20T14:00:00',
      prix: 120,
      bus: { id: 1, immatriculation: '12345-A-6', capacite: 40, type: 'Confort' },
    };
    component.seatNumber = 14;
    component.reservation = {
      id: 10,
      tripId: 1,
      travelerId: 1,
      seatId: 14,
      status: 'EN_ATTENTE',
      reservationDate: '2026-11-20T10:00:00',
      expiresAt: '2026-11-20T10:10:00',
      confirmedAt: null,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit pay event when onConfirmPayment is called', () => {
    let emitted: any = null;
    component.pay.subscribe((val) => (emitted = val));

    component.selectedMethod = 'CARTE_BANCAIRE';
    component.onConfirmPayment();

    expect(emitted).toBeTruthy();
    expect(emitted.paymentMethod).toBe('CARTE_BANCAIRE');
  });
});
