import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TripSearch } from './trip-search';
import { todayDate } from '../../../../shared/utils/travel-date';

describe('TripSearch', () => {
  let component: TripSearch;
  let fixture: ComponentFixture<TripSearch>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripSearch],
    }).compileComponents();

    fixture = TestBed.createComponent(TripSearch);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('uses today for a popular route when the date is missing or stale', () => {
    const emit = vi.spyOn(component.search, 'emit');
    component.selectRoute('Agadir', 'Casablanca');
    expect(component.date).toBe(todayDate());
    expect(emit).toHaveBeenLastCalledWith({ departure: 'Agadir', destination: 'Casablanca', date: todayDate() });
    component.date = '2020-01-01';
    component.selectRoute('Agadir', 'Marrakech');
    expect(component.date).toBe(todayDate());
  });

  it('preserves a future date when selecting a popular route', () => {
    component.date = '2099-12-02';
    component.selectRoute('Agadir', 'Marrakech');
    expect(component.date).toBe('2099-12-02');
  });
});
