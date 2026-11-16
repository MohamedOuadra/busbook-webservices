import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JourneyCard } from './journey-card';

describe('JourneyCard', () => {
  let component: JourneyCard;
  let fixture: ComponentFixture<JourneyCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JourneyCard],
    }).compileComponents();

    fixture = TestBed.createComponent(JourneyCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
