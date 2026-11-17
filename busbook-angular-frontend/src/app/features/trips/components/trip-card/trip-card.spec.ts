import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TripCard } from './trip-card';

describe('TripCard', () => {
  let component: TripCard;
  let fixture: ComponentFixture<TripCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripCard],
    }).compileComponents();

    fixture = TestBed.createComponent(TripCard);
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
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
