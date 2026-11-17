import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { BookingPanel } from './booking-panel';

describe('BookingPanel', () => {
  let component: BookingPanel;
  let fixture: ComponentFixture<BookingPanel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookingPanel],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BookingPanel);
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
