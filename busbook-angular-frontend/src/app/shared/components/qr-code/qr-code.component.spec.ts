import { ComponentFixture, TestBed } from '@angular/core/testing';
import { QrCodeComponent } from './qr-code.component';

describe('QrCodeComponent', () => {
  let component: QrCodeComponent;
  let fixture: ComponentFixture<QrCodeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QrCodeComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(QrCodeComponent);
    component = fixture.componentInstance;
    component.value = 'TKT-TEST-12345';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should generate matrix when value is provided', () => {
    component.value = 'SAMPLE-QR-DATA';
    component.ngOnChanges();
    expect(component.matrix.length).toBeGreaterThan(0);
    expect(component.matrix[0].length).toBeGreaterThan(0);
  });
});
