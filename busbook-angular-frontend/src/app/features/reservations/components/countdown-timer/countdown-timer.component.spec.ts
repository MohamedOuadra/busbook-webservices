import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CountdownTimerComponent } from './countdown-timer.component';

describe('CountdownTimerComponent', () => {
  let component: CountdownTimerComponent;
  let fixture: ComponentFixture<CountdownTimerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CountdownTimerComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CountdownTimerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should format 600 seconds as 10:00', () => {
    component.seconds = 600;
    expect(component.formattedTime).toBe('10:00');
  });

  it('should format 59 seconds as 00:59', () => {
    component.seconds = 59;
    expect(component.formattedTime).toBe('00:59');
  });

  it('should identify warning state when seconds <= 120 and > 0', () => {
    component.seconds = 120;
    expect(component.isWarning).toBe(true);

    component.seconds = 300;
    expect(component.isWarning).toBe(false);
  });

  it('should report 0 progress when expired', () => {
    component.isExpired = true;
    component.seconds = 0;
    expect(component.progressPercentage).toBe(0);
  });
});
