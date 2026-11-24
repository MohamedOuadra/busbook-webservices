import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TripSearch } from './trip-search';
import { todayDate } from '../../../../shared/utils/travel-date';

describe('TripSearch', () => {
  let component: TripSearch;
  let fixture: ComponentFixture<TripSearch>;

  beforeEach(async () => {
    localStorage.clear();
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

  it('replays stale searches using today and records the updated date', () => {
    const emit = vi.spyOn(component.search, 'emit');
    component.repeatSearch({ departure: 'Agadir', destination: 'Rabat', date: '2020-01-01' });
    expect(emit).toHaveBeenCalledWith({ departure: 'Agadir', destination: 'Rabat', date: todayDate() });
    expect(component.history.recent()[0].date).toBe(todayDate());
  });

  it('continues emitting searches when history cannot be saved', () => {
    vi.spyOn(component.history, 'remember').mockReturnValue(false);
    const emit = vi.spyOn(component.search, 'emit');
    component.selectRoute('Agadir', 'Rabat');
    expect(emit).toHaveBeenCalled();
    expect(component.storageMessage()).toContain('could not be saved');
  });

  it('renders recent searches and clears them from the UI', () => {
    component.selectRoute('Agadir', 'Casablanca');
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Recent searches');
    const clear = Array.from(fixture.nativeElement.querySelectorAll('button'))
      .find((button: any) => button.textContent.trim() === 'Clear history') as HTMLButtonElement;
    clear.click();
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).not.toContain('Recent searches');
  });
});
