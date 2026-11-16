import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BusIllustration } from './bus-illustration';

describe('BusIllustration', () => {
  let component: BusIllustration;
  let fixture: ComponentFixture<BusIllustration>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BusIllustration],
    }).compileComponents();

    fixture = TestBed.createComponent(BusIllustration);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
