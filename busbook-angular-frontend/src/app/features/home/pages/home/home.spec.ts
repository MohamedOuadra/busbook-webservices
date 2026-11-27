import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';
import { TripService } from '../../../../core/services/trip';
import { Trip } from '../../../trips/models/trip';
import { Home } from './home';

describe('Home', () => {
  let component: Home;
  let fixture: ComponentFixture<Home>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [provideRouter([]), { provide: TripService, useValue: {
        getTrips: () => of([]), searchTrips: () => of([]),
      } }],
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  const trips: Trip[] = [
    { id: 1, prix: 200, dateDepart: '2026-12-02T12:00:00', dateArrivee: '', villeDepart: 'Agadir', villeArrivee: 'Casablanca', bus: { id: 1, type: 'VIP', capacite: 40, immatriculation: 'A' } },
    { id: 2, prix: 100, dateDepart: '2026-12-02T08:00:00', dateArrivee: '', villeDepart: 'Agadir', villeArrivee: 'Casablanca', bus: { id: 2, type: 'Standard', capacite: 40, immatriculation: 'B' } },
    { id: 3, prix: 150, dateDepart: '2026-12-02T10:00:00', dateArrivee: '', villeDepart: 'Agadir', villeArrivee: 'Casablanca', bus: { id: 3, type: 'VIP', capacite: 40, immatriculation: 'C' } },
  ];

  it('sorts prices in both directions without changing catalog order', () => {
    component.trips = trips;
    component.sortOrder.set('price-asc');
    expect(component.visibleTrips().map(t => t.id)).toEqual([2, 3, 1]);
    component.sortOrder.set('price-desc');
    expect(component.visibleTrips().map(t => t.id)).toEqual([1, 3, 2]);
    component.sortOrder.set('default');
    expect(component.visibleTrips()).toEqual(trips);
    expect(trips.map(t => t.id)).toEqual([1, 2, 3]);
  });

  it('sorts trips by departure time', () => {
    component.trips = trips;
    component.sortOrder.set('departure');
    expect(component.visibleTrips().map(t => t.id)).toEqual([2, 3, 1]);
  });

  it('keeps the existing reservation navigation', () => {
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    component.onTripSelected(trips[0]);
    expect(navigate).toHaveBeenCalledWith(['/reservation', 1]);
  });

  it('combines inclusive price and bus filters before sorting', () => {
    component.trips = trips;
    component.maxPrice.set(150);
    component.busType.set('VIP');
    component.sortOrder.set('price-asc');
    expect(component.visibleTrips().map(t => t.id)).toEqual([3]);
    component.maxPrice.set(0);
    expect(component.visibleTrips()).toEqual([]);
    component.clearResultFilters();
    expect(component.visibleTrips().map(t => t.id)).toEqual([2, 3, 1]);
  });

  it('clears result filters without requesting a new catalog', () => {
    component.trips = trips;
    component.maxPrice.set(1);
    component.clearResultFilters();
    expect(component.trips).toBe(trips);
    expect(component.visibleTrips()).toEqual(trips);
    expect(component.busTypes()).toEqual(['Standard', 'VIP']);
  });

  it('shows a recoverable empty state and updates the rendered count', () => {
    component.trips = trips;
    component.maxPrice.set(1);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('0 of 3 trips available');
    expect(fixture.nativeElement.textContent).toContain('No trips match these filters');
    const clearButton = Array.from(fixture.nativeElement.querySelectorAll('button'))
      .find((button: any) => button.textContent.trim() === 'Clear filters') as HTMLButtonElement;
    clearButton.click();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('app-trip-card')).toHaveLength(3);
  });
});
