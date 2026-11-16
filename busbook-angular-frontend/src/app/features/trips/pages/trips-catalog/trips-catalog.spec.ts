import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { TripsCatalogPage } from './trips-catalog';

describe('TripsCatalogPage', () => {
  let component: TripsCatalogPage;
  let fixture: ComponentFixture<TripsCatalogPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripsCatalogPage],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(TripsCatalogPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
