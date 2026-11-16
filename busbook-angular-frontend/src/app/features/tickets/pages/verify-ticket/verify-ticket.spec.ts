import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { VerifyTicketPage } from './verify-ticket';

describe('VerifyTicketPage', () => {
  let component: VerifyTicketPage;
  let fixture: ComponentFixture<VerifyTicketPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifyTicketPage],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VerifyTicketPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
