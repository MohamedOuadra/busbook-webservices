import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MyReservationsPage } from './my-reservations';

describe('MyReservationsPage', () => {
  let component: MyReservationsPage;
  let fixture: ComponentFixture<MyReservationsPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyReservationsPage],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MyReservationsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
